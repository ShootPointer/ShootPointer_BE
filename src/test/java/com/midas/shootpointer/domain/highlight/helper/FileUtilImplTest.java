package com.midas.shootpointer.domain.highlight.helper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class FileUtilImplTest {
    @InjectMocks
    private FileUtilImpl fileUtil;

    @TempDir
    private Path tempDir;

    @Test
    @DisplayName("파일의 경로를 성공적으로 반환합니다._SUCCESS")
    void getDirectoryPath_SUCCESS(){
        //given
        String highlightKey="testVideo";


        //when

        //then
    }

    @Test
    @DisplayName("파일의 경로를 읽기 실패한 경우 FILE_UPLOAD_FAILED 예외를 발생시킵니다._FAIL")
    void getDirectoryPath_FAIL(){
        //given

        //when

        //then
    }
}