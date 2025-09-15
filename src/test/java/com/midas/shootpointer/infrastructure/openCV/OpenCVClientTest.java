package com.midas.shootpointer.infrastructure.openCV;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.global.util.file.GenerateFileName;
import com.midas.shootpointer.infrastructure.openCV.dto.OpenCVResponse;
import com.midas.shootpointer.infrastructure.openCV.service.OpenCVClientImpl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OpenCVClientTest {
    private OpenCVClientImpl openCVClient;
    private MockWebServer mockWebServer;
    private static ObjectMapper objectMapper;

    @Mock
    private GenerateFileName generateFileName;

    @Mock
    private OpenCVProperties openCVProperties;

    @BeforeEach
    void setUpServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        objectMapper = new ObjectMapper();

        setUpMockProperties();
        setupOpenCVClient();

        when(generateFileName.generate(any(), any())).thenReturn("test-image.png");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("OpenCV 서버로 이미지와 유저 정보를 전송합니다.-SUCCESS")
    void sendBackNumberInformation_SUCCESS() throws IOException, InterruptedException {
        //given
        //Mock Response JSON
        OpenCVResponse openCVResponse = OpenCVResponse
                .builder()
                .success(true)
                .status(200)
                .build();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(openCVResponse))
                .addHeader("Content-Type", "application/json"));
        /*
         * Mock 이미지
         */
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "image",
                "test-image.png",
                "image/png",
                "fake img".getBytes()
        );
        Integer mockBackNumber = 1;
        UUID mockUserId = UUID.randomUUID();
        String mockToken="test-token";

        //when
       // OpenCVResponse<?> response = openCVClient.sendBackNumberInformation(mockUserId, mockBackNumber, mockMultipartFile,mockToken);

        //then
       // assertThat(response.isSuccess()).isTrue();
        //assertThat(response.getStatus()).isEqualTo(200);

        /**
         * 요청 방식 일치
         */
        RecordedRequest request = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        assertThat(request).isNotNull();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).contains("/api/send-image");

        assertThat(request.getHeader("Authorization")).isEqualTo("test-token");
        assertThat(request.getHeader("X-Member-Id")).isEqualTo(mockUserId.toString());

        assertThat(request.getHeader("Content-Type")).startsWith("multipart/form-data");

        String body = request.getBody().readUtf8();
        assertThat(body).contains("name=\"image\"");
        assertThat(body).contains("filename=\"test-image.png\"");
        assertThat(body).contains("fake img");
        assertThat(body).contains("name=\"backNumber\"");
        assertThat(body).contains("1");
    }

    @Test
    @DisplayName("OpenCV 서버로 이미지와 유저 정보를 전송시 읽기 에러 발생 시 CustomException이 발생하고 3번 재시도 합니다.-FAIL")
    void sendBackNumberInformation_Retry3Times_FAIL() {
        //given
        for (int i = 0; i < 4; i++) {
            mockWebServer.enqueue(new MockResponse()
                    .setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        }

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file", "test.img", "image/png", "fake img".getBytes()
        );
        UUID mockUserId = UUID.randomUUID();
        String mockToken="test-token";
        // when
       // CustomException exception = catchThrowableOfType(() ->
       //                 openCVClient.sendBackNumberInformation(mockUserId, 1, mockMultipartFile,mockToken),
         //       CustomException.class);

        // then
        //assertThat(exception).isNotNull();
        //assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FAILED_POST_API_RETRY_TO_OPENCV);

        // 요청이 4번 발생 확인
        assertThat(mockWebServer.getRequestCount()).isEqualTo(4);

    }

    @Test
    @DisplayName("OpenCV 서버로 이미지와 유저 정보를 전송시 openCV에서 응답값이 없을 시 CustomException를 반환합니다.-FAIL")
    void sendBackNumberInformation_Of_OpenCVNullResponse_Error_FAIL() throws IOException, InterruptedException {
        //given
        //Mock Response JSON
        OpenCVResponse openCVResponse = null;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(openCVResponse))
                .addHeader("Content-Type", "application/json"));
        /*
         * Mock 이미지
         */
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "test.img",
                "image/png",
                "fake img".getBytes()
        );
        Integer mockBackNumber = 1;
        UUID mockUserId = UUID.randomUUID();
        String mockToken="test-token";
        //when & then
        /*CustomException customException=catchThrowableOfType(()->
                openCVClient.sendBackNumberInformation(mockUserId, mockBackNumber, mockMultipartFile,mockToken),
                CustomException.class
        );*/
        /*assertThat(customException).isNotNull();
        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.FAILED_SEND_IMAGE_TO_OPENCV);*/
    }

    @Test
    @DisplayName("OpenCV 서버로 이미지와 유저 정보를 전송시 openCV에서 응답값의 sucess가 false 반환 시 CustomException를 반환합니다.-FAIL")
    void sendBackNumberInformation_Of_OpenCVInternal_Error_FAIL() throws IOException, InterruptedException {
        //given
        //Mock Response JSON
        OpenCVResponse openCVResponse = OpenCVResponse.builder()
                .success(false)
                .status(400)
                .build();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(openCVResponse))
                .addHeader("Content-Type", "application/json"));
        /*
         * Mock 이미지
         */
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "test.img",
                "image/png",
                "fake img".getBytes()
        );
        Integer mockBackNumber = 1;
        UUID mockUserId = UUID.randomUUID();
        String mockToken="test-token";

        //when & then
        /*CustomException customException=catchThrowableOfType(()->
                        openCVClient.sendBackNumberInformation(mockUserId, mockBackNumber, mockMultipartFile,mockToken),
                CustomException.class
        );*/
        //assertThat(customException).isNotNull();
        //assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.FAILED_SEND_IMAGE_TO_OPENCV);
    }

    private void setUpMockProperties() {
        OpenCVProperties.Api api = new OpenCVProperties.Api();
        OpenCVProperties.Api.Post post = new OpenCVProperties.Api.Post();
        OpenCVProperties.Api.Get get = new OpenCVProperties.Api.Get();

        /**
         * API 경로
         */
        post.setSendImage("/api/send-image");
        get.setFetchVideo("/api/fetch-video");
        api.setPost(post);
        api.setGet(get);

        when(openCVProperties.getUrl()).thenReturn(mockWebServer.url("/").toString());
        when(openCVProperties.getApi()).thenReturn(api);
    }

    private void setupOpenCVClient() {
        openCVClient = new OpenCVClientImpl(generateFileName, openCVProperties);
    }
}