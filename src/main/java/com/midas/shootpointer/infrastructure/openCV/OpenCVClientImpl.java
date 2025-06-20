package com.midas.shootpointer.infrastructure.openCV;

import com.midas.shootpointer.global.annotation.CustomLog;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import com.midas.shootpointer.global.util.file.FileType;
import com.midas.shootpointer.global.util.file.GenerateFileName;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.UUID;

@Component
public class OpenCVClientImpl implements OpenCVClient {
    private final GenerateFileName generateFileName;

    private final WebClient webClient;

    public OpenCVClientImpl(GenerateFileName generateFileName,
                            @Value("${openCV.url}")String openCVUrl){
        this.webClient= WebClient.builder().baseUrl(openCVUrl).build();
        this.generateFileName=generateFileName;
    }
    /**
     * openCV 서버 주소
     */
    @Value("${openCV.url}")
    private String openCVUrl;

    /**
     * openCV 사진 전송 api 주소
     */
    @Value("${openCV.api.post.send-img}")
    private String openCVPostApiUrl;

    /**
     * openCV 비디오 api 주소
     */
    @Value("${openCV.api.get.fetch-video}")
    private String openCVGetApiUrl;


    /*==========================
    *
    *OpenCVClientImpl
    *
    * @parm userId : 멤버 ID , backNumber : 등 번호 , image : 등 번호 이미지
    * @return void
    * @author kimdoyeon
    * @version 1.0.0
    * @date 6/17/25
    *
    ==========================**/
    @Override
    @CustomLog
    public void sendBackNumberInformation(UUID userId, Integer backNumber, MultipartFile image) throws IOException {
        //이미지 이름 생성
        String fileName = generateFileName.generate(FileType.IMAGE, image);

        webClient.post()
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
                .bodyToMono(Void.class)
                .block();
    }
}
