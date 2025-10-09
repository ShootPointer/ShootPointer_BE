package com.midas.shootpointer.global.util.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileValidatorImplTest {

    @Test
    @DisplayName("파일 사이즈 크기를 초과하면 FILE_SIZE_EXCEEDED 예외를 발생합니다.")
    void isValidFileSize() {
        //given
        long fileSize=2000000L;

        //when

        //then

    }

    @Test
    void isValidFileType() {
    }
}