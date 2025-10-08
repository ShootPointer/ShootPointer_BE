package com.midas.shootpointer.domain.highlight.service;

import com.midas.shootpointer.domain.highlight.helper.HighlightHelper;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HighlightStorageServiceTest {

    @InjectMocks
    private HighlightStorageService storageService;

    @Mock
    private HighlightHelper highlightHelper;

    @TempDir
    private Path tempDir;

    @Test
    @DisplayName("파일이 지정된 디렉토리에 저장되고 저장된 파일 이름이 반환됩니다.")
    void storeHighlights_SUCCESS() throws IOException {
        //given
        String highlightKey= UUID.randomUUID().toString();
        Path dirPath=tempDir.resolve(highlightKey);
        Files.createDirectories(dirPath);

        when(highlightHelper.getDirectoryPath(highlightKey))
                .thenReturn(dirPath.toString());
        byte[] fileContent="video".getBytes();
        MultipartFile file1=new MockMultipartFile("file1","video1.mp4","video/mp4",fileContent);
        MultipartFile file2=new MockMultipartFile("file2","video2.mp4","video/mp4",fileContent);
        List<MultipartFile> files=List.of(file1,file2);

        //when
        List<String> result=storageService.storeHighlights(files,highlightKey);

        //then
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).endsWith(".mp4");
        assertThat(Files.list(dirPath).count()).isEqualTo(2);

        verify(highlightHelper).getDirectoryPath(highlightKey);
    }
    @Test
    @DisplayName("파일 저장 중 IOException이 발생하면 FILE_UPLOAD_FAILED을 반환합니다.")
    void storeHighlights_failToSaveFile() throws Exception {
        // given
        String highlightKey = "testKey";
        when(highlightHelper.getDirectoryPath(highlightKey)).thenReturn("/invalid/path"); // 존재하지 않는 경로

        MultipartFile badFile = new MockMultipartFile("file", "bad.mp4", "video/mp4", new byte[1]);

        // when & then
        assertThatThrownBy(() -> storageService.storeHighlights(List.of(badFile), highlightKey))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.FILE_UPLOAD_FAILED.getMessage());
    }
}