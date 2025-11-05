package com.midas.shootpointer.global.util.file;

import com.midas.shootpointer.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.unit.DataSize;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileValidatorImplTest {

    private final FileValidatorImpl fileValidator = new FileValidatorImpl(DataSize.ofMegabytes(200));

    @Test
    @DisplayName("허용된 용량 이하의 파일은 검증을 통과한다")
    void validateFileSize() {
        long validSize = DataSize.ofMegabytes(199).toBytes();

        assertThatCode(() -> fileValidator.isValidFileSize(validSize))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("최대 용량을 초과하면 예외가 발생한다")
    void validateFileSizeExceeded() {
        long invalidSize = DataSize.ofMegabytes(201).toBytes();

        assertThatThrownBy(() -> fileValidator.isValidFileSize(invalidSize))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("MP4 확장자를 가진 파일만 허용한다")
    void validateFileType() {
        assertThatCode(() -> fileValidator.isValidFileType("highlight.mp4"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("잘못된 확장자의 경우 예외가 발생한다")
    void validateFileTypeInvalid() {
        assertThatThrownBy(() -> fileValidator.isValidFileType("highlight.mov"))
                .isInstanceOf(CustomException.class);
    }
}
