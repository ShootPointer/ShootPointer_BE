package com.midas.shootpointer.infrastructure.openCV;

import com.midas.shootpointer.global.util.file.FileType;
import com.midas.shootpointer.global.util.file.GenerateFileName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

class OpenCVClientTest {
    private static final String OPENCV_URL="http://localhost:8888";
    private static final String OPENCV_IMAGE_API_URL="back-number";

    private OpenCVClient openCVClient;
    private GenerateFileName generateFileName;
    private WebClient webClient;
    private WebClient.Builder builder;



    @BeforeEach
    void init(){
        this.webClient=builder.baseUrl(OPENCV_URL).build();
    }

    @Test
    @DisplayName("OpenCV 서버로 이미지와 유저 정보를 전송합니다.-SUCCESS")
    void sendBackNumberInformation_SUCCESS(){
        //given
        /*
         * Mock 이미지
         */
        MockMultipartFile mockMultipartFile=new MockMultipartFile(
                "file",
                "test.img",
                "image/png",
                "fake img".getBytes()
        );

        String mockImageFileName=generateFileName.generate(FileType.IMAGE,mockMultipartFile);

        //when

        //then
    }
}