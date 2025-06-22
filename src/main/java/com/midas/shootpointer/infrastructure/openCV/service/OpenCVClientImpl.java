package com.midas.shootpointer.infrastructure.openCV.service;

import com.midas.shootpointer.global.annotation.CustomLog;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import com.midas.shootpointer.global.util.file.FileType;
import com.midas.shootpointer.global.util.file.GenerateFileName;
import com.midas.shootpointer.infrastructure.openCV.OpenCVProperties;
import com.midas.shootpointer.infrastructure.openCV.dto.OpenCVResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Component
public class OpenCVClientImpl implements OpenCVClient {
    private final GenerateFileName generateFileName;

    private final WebClient webClient;
    private final String openCVGetApiUrl;
    private final String openCVPostApiUrl;

    public OpenCVClientImpl(
            GenerateFileName generateFileName,
            OpenCVProperties openCVProperties
    ) {
        this.webClient = WebClient.builder().baseUrl(openCVProperties.getUrl()).build();
        this.generateFileName = generateFileName;
        this.openCVGetApiUrl = openCVProperties.getApi().getGet().getFetchVideo();
        this.openCVPostApiUrl = openCVProperties.getApi().getPost().getSendImage();
    }


    /*==========================
    *
    *OpenCVClientImpl
    *
    * @parm userId : 멤버 ID , backNumber : 등 번호 , image : 등 번호 이미지
    * @return OpenCVResponse 응답값
    * @author kimdoyeon
    * @version 1.0.0
    * @date 6/17/25
    *
    ==========================**/
    @Override
    @CustomLog
    public OpenCVResponse<?> sendBackNumberInformation(UUID userId, Integer backNumber, MultipartFile image) throws IOException {
        //이미지 이름 생성
        String fileName = generateFileName.generate(FileType.IMAGE, image);

        OpenCVResponse<?> response = webClient.post()
                .uri(openCVPostApiUrl)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("image", new ByteArrayResource(image.getBytes()) {
                            @Override
                            public String getFilename() {
                                return fileName;
                            }
                        })
                        .with("userId", userId.toString())
                        .with("backNumber", backNumber.toString()))
                .retrieve()
                .bodyToMono(OpenCVResponse.class)
                //최대 5초 연결 시간
                .timeout(Duration.ofSeconds(5))
                //최대 3번 실행
                //1초 간격
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        //읽기 예외 혹은 시간초과 예외에 대해서 재시도 실행
                        .filter(e ->
                                e instanceof IOException ||
                                        e instanceof TimeoutException)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new CustomException(ErrorCode.FAILED_POST_API_RETRY_TO_OPENCV))
                )
                //동기식 처리
                .block();

        //openCV 응답값 예외 처리
        validateOpenCVResponse(response);

        return response;
    }

    /*==========================
    *
    *OpenCVClientImpl
    *
    * @parm OpenCVResponse : OpenCV 서버 응답 json
    * @return void
    * @author kimdoyeon
    * @version 1.0.0
    * @date 6/21/25
    *
    ==========================**/
    private void validateOpenCVResponse(OpenCVResponse<?> response) {
        if (response == null || !response.isSuccess()) {
            throw new CustomException(ErrorCode.FAILED_SEND_IMAGE_TO_OPENCV);
        }
    }
}
