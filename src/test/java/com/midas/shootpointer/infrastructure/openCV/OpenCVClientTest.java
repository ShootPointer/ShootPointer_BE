package com.midas.shootpointer.infrastructure.openCV;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.global.util.file.GenerateFileName;
import com.midas.shootpointer.infrastructure.openCV.dto.OpenCVResponse;
import com.midas.shootpointer.infrastructure.openCV.service.OpenCVClientImpl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import static org.assertj.core.api.Assertions.*;

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
        mockWebServer=new MockWebServer();
        mockWebServer.start();
        objectMapper = new ObjectMapper();

        setUpMockProperties();
        setupOpenCVClient();

        when(generateFileName.generate(any(),any())).thenReturn("test-image.png");
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
                "file",
                "test.img",
                "image/png",
                "fake img".getBytes()
        );
        Integer mockBackNumber = 1;
        UUID mockUserId = UUID.randomUUID();

        //when
        OpenCVResponse<?> response = openCVClient.sendBackNumberInformation(mockUserId, mockBackNumber, mockMultipartFile);

        //then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getStatus()).isEqualTo(200);

        /**
         * 요청 방식 일치
         */
        RecordedRequest request=mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        assertThat(request).isNotNull();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/api/send-image");
        assertThat(request.getHeader("Content-Type")).startsWith("multipart/form-data");

        /**
         * 요청 값 일치
         */
        String requestBody=request.getBody().readUtf8();
        assertThat(requestBody).contains("name=\"userId\"");
        assertThat(requestBody).contains(mockUserId.toString());
        assertThat(requestBody).contains("name=\"backNumber\"");
        assertThat(requestBody).contains("1");
        assertThat(requestBody).contains("filename=\"test-image.png\"");
        assertThat(requestBody).contains("fake img");
    }

    private void setUpMockProperties(){
        OpenCVProperties.Api api=new OpenCVProperties.Api();
        OpenCVProperties.Api.Post post=new OpenCVProperties.Api.Post();
        OpenCVProperties.Api.Get get=new OpenCVProperties.Api.Get();

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

    private void setupOpenCVClient(){
        openCVClient=new OpenCVClientImpl(generateFileName,openCVProperties);
    }
}