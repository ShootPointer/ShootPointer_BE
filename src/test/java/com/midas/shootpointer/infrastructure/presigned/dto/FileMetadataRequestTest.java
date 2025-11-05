package com.midas.shootpointer.infrastructure.presigned.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileMetadataRequestTest {
    @Test
    @DisplayName("fileName, filesize를 입력하여 of 메서드로 FileMetadataRequest 객체를 생성합니다.")
    void of(){
        //given
        String fileName="fileName";
        Long fileSize=123L;

        //when
        FileMetadataRequest request=FileMetadataRequest.of(fileName,fileSize);

        //then
        assertThat(request.fileName()).isEqualTo(fileName);
        assertThat(request.fileSize()).isEqualTo(fileSize);
    }

}