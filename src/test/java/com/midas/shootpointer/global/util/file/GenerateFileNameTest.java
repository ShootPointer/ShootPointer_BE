package com.midas.shootpointer.global.util.file;

import com.midas.shootpointer.global.util.uuid.UUIDHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@ExtendWith({MockitoExtension.class})
class GenerateFileNameTest {
    private GenerateFileName generateFileName;

    private final UUIDHandler mockUUIDHandler = ()-> UUID.fromString("123e4567-e89b-12d3-a456-426614174000");


    @BeforeEach
    void setUp(){
        generateFileName=new GenerateFileName(mockUUIDHandler);
    }
    @Test
    @DisplayName("파일 타입과 파일을 파라미터로 파일 이름을 생성 합니다.")
    void GenerateFileName_SUCCESS(){
        //given
        String originalFileName="test.img";
        MockMultipartFile mockMultipartFile=new MockMultipartFile(
                "file",
                originalFileName,
                "image/png",
                "fake img".getBytes()
        );

        //when
        String generatedName=generateFileName.generate(FileType.IMAGE,mockMultipartFile);

        //then
        assertThat(generatedName).isEqualTo("123e4567-e89b-12d3-a456-426614174000_image.img");
    }

}