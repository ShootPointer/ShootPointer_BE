package com.midas.shootpointer.global.util.file;

import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.unit.DataSize;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileValidatorImplTest {

    //200MB 기준
    private FileValidatorImpl fileValidator=new FileValidatorImpl(DataSize.ofMegabytes(200));

    @Test
    @DisplayName("파일 사이즈 크기를 초과하면 FILE_SIZE_EXCEEDED 예외를 발생합니다.")
    void isValidFileSize_EXCEPTION() {
        //given
        long overSize=20000000000L;

        //when & then
        assertThatThrownBy(()->fileValidator.isValidFileSize(overSize))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.FILE_SIZE_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("파일 사이즈가 허용 범위 내이면 예외가 발생하지 않습니다.")
    void isValidFileSize_SUCCESS(){
        //given
        long size=1000L;

        //when
        fileValidator.isValidFileSize(size);
    }

    @Test
    @DisplayName("파일 타입이 Null이거나 비어 있으면 INVALID_FILE_TYPE 예외를 발생합니다.")
    void isValidFileType_IS_BLANK_OR_NULL() {
        //when & then
        assertThatThrownBy(()->fileValidator.isValidFileType(null))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_FILE_TYPE.getMessage());

        assertThatThrownBy(()->fileValidator.isValidFileType(""))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_FILE_TYPE.getMessage());
    }

    @Test
    @DisplayName("파일 타입이 mp4가 아니면 INVALID_FILE_TYPE 예외를 발생합니다.")
    void isValidFileType_NOT_MP4(){
        //given
        String fileType="file.mp3";

        //when & then
        assertThatThrownBy(()->fileValidator.isValidFileType(fileType))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_FILE_TYPE.getMessage());

    }

    @Test
    @DisplayName("파일이 mp4형식이면 예외가 발생하지 않습니다.")
    void isValidFileType(){
        //given
        String fileType="file.mp4";

        //when & then
        fileValidator.isValidFileType(fileType);
    }
}