package com.midas.shootpointer.infrastructure.openCV;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.global.util.file.FileType;
import com.midas.shootpointer.global.util.file.GenerateFileName;
import com.midas.shootpointer.infrastructure.openCV.dto.OpenCVResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.UUID;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OpenCVClientTest {

    @Autowired
    private OpenCVClient openCVClient;
    @Autowired
    private GenerateFileName generateFileName;
    private static MockWebServer mockWebServer;
    private static ObjectMapper objectMapper;

    @Value("${openCV.url}")
    private String OPENCV_URL;

    @Value("${openCV.api.post.send-img}")
    private String OPENCV_IMAGE_API_URL;

    @Value("${openCV.api.get.fetch-video}")
    private String OPENCV_VIDEO_API_URL;


    private String openCVImageTestUrl;
    private String openCVVideoTestUrl;



    @BeforeAll
    void setUpServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void setUpEach(){
        openCVImageTestUrl=mockWebServer.url(OPENCV_IMAGE_API_URL).toString();
        openCVVideoTestUrl=mockWebServer.url(OPENCV_VIDEO_API_URL).toString();
    }
    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    @DisplayName("OpenCV 서버로 이미지와 유저 정보를 전송합니다.-SUCCESS")
    void sendBackNumberInformation_SUCCESS() throws IOException {
        //given
        //Mock Response JSON
        OpenCVResponse openCVResponse = OpenCVResponse
                .builder()
                .success(true)
                .status(HttpStatus.OK)
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

        String mockImageFileName = generateFileName.generate(FileType.IMAGE, mockMultipartFile);
        Integer mockBackNumber = 1;
        //when

        //then
    }
}