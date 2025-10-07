package com.midas.shootpointer.domain.highlight.helper;

import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HighlightValidatorImplTest {
    @InjectMocks
    @Spy
    private HighlightValidatorImpl highlightValidator;


    @Test
    @DisplayName("디렉토리의 경로가 존재하면 true를 반환합니다.")
    void filesExist_TRUE() throws IOException {
        //given
        Path tempDir = Files.createTempDirectory("test");
        String dir = tempDir.toString();

        //then
        assertThat(highlightValidator.filesExist(dir)).isTrue();

        //cleanup
        Files.deleteIfExists(tempDir);
    }

    @Test
    @DisplayName("디렉토리의 경로가 존재하지 않으면 false를 반환합니다.")
    void filesExist_FALSE() throws IOException {
        //given
        String dir="dir";

        //then
        assertThat(highlightValidator.filesExist(dir)).isFalse();

    }

    @Test
    @DisplayName("존재하지 않은 하이라이트 객체를 조회시 NOT_EXIST_HIGHLIGHT 예외를 반환합니다.")
    void isExistHighlightId() {
        //given
        UUID highlightId=UUID.randomUUID();

        //when
        CustomException exception=catchThrowableOfType(()->
                highlightValidator.isExistHighlightId(highlightId),
                CustomException.class
        );

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_EXIST_HIGHLIGHT);

    }

    @Test
    @DisplayName("유저의 하이라이트 영상이 아닌 경우 NOT_MATCH_HIGHLIGHT_VIDEO 예외를 반환합니다.")
    void isValidMembersHighlight() {
        //given
        UUID memberId=UUID.randomUUID();
        UUID highlightId=UUID.randomUUID();

        //when
        CustomException exception=catchThrowableOfType(()->
                        highlightValidator.isValidMembersHighlight(highlightId,memberId),
                CustomException.class
        );

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_MATCH_HIGHLIGHT_VIDEO);
    }

    @Test
    @DisplayName("파일의 타입이 video/mp4가 아니면 INVALID_FILE_TYPE를 반환합니다.")
    void isValidMp4File() {
        //given
        MultipartFile file = new MockMultipartFile(
                "file",
                "video.mp3",
                "video/mp3",
                new byte[10]
        );
        //when
        CustomException exception = catchThrowableOfType(
                () -> highlightValidator.isValidMp4File(file),
                CustomException.class
        );

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_FILE_TYPE);

    }

    @Test
    @DisplayName("파일의 크기가 MAX_FILE_SIZE 보다 큰 경우 FILE_SIZE_EXCEEDED 예외를 반환합니다.")
    void isValidFileSize() {
        //given
        long size=600L * 1024L * 1024L;
        byte[] content = new byte[(int) (10 * 1024)];
        MultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", content);

        MultipartFile spyFile = spy(file);
        when(spyFile.getSize()).thenReturn(size);

        //when & then
        CustomException exception=catchThrowableOfType(()->
                highlightValidator.isValidFileSize(spyFile),
                CustomException.class
        );
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FILE_SIZE_EXCEEDED);

    }

    @Test
    @DisplayName("존재하는 디렉토리인 경우 true를 반환합니다.")
    void isExistDirectory_TRUE() throws IOException {
        //given
        Path tempPath=Files.createTempDirectory("test");

        //when & then
        assertThat(highlightValidator.filesExist(tempPath.toString())).isTrue();

    }

    @Test
    @DisplayName("디렉토리가 null 혹은 공백이면 false를 반환합니다.")
    void isExistDirectory_null_or_blank_FALSE() throws IOException {
        //given
        String tempDir1=" ";
        String tempDir2="";
        String tempDir3=null;

        //when & then
        assertThat(highlightValidator.isExistDirectory(tempDir1)).isFalse();
        assertThat(highlightValidator.isExistDirectory(tempDir2)).isFalse();
        assertThat(highlightValidator.isExistDirectory(tempDir3)).isFalse();

    }

    @Test
    @DisplayName("존재하지 않는 디렉토리이면 false를 반환합니다.")
    void isExistDirectory_NOT_EXIST_DIRECTORY_FALSE() throws IOException {
        //given
        String tempDir="not-exist";

        //when & then
        assertThat(highlightValidator.isExistDirectory(tempDir)).isFalse();
    }

    @Test
    @DisplayName("여러 파일들의 사이즈 유효성 검증과 mp4파일 유효성 검증을 실시합니다.")
    void areValidFiles(){
        //given
        byte[] content = new byte[(int) (10 * 1024)];
        MultipartFile file1 = new MockMultipartFile(
                "file",
                "test1.mp4",
                "video/mp4",
                content
        );
        MultipartFile file2 = new MockMultipartFile(
                "file",
                "test2.mp4",
                "video/mp4",
                content
        );

        List<MultipartFile> files=List.of(file1,file2);

        //when
        highlightValidator.areValidFiles(files);

        //then
        verify(highlightValidator,times(2))
                .isValidFileSize(any(MultipartFile.class));
        verify(highlightValidator,times(2))
                .isValidMp4File(any(MultipartFile.class));
    }
}