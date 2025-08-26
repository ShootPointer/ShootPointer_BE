package com.midas.shootpointer.domain.highlight.helper;

import com.midas.shootpointer.domain.highlight.repository.HighlightQueryRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
class FileValidationImplTest {
    @InjectMocks
    private FileValidationImpl fileValidation;

    @Mock
    private HighlightQueryRepository highlightQueryRepository;

    @Test
    @DisplayName("파일의 크기가 100MB를 초과하면 FILE_SIZE_EXCEEDED 예외를 발생시킵니다._FAIL")
    void isValidFileSize() throws IOException {
        //given
        byte[] bytes = new byte[101 * 1024 * 1024];//파일 크기 101MB로 설정.
        String fileType = "mp3";
        MockMultipartFile mockMultiFile = mockMultipartFile(bytes, fileType);

        //when & then
        CustomException customException = catchThrowableOfType(() ->
                        fileValidation.isValidFileSize(mockMultiFile),
                CustomException.class
        );
        assertThat(customException).isNotNull();
        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.FILE_SIZE_EXCEEDED);
    }

    @Test
    @DisplayName("파일의 타입이 mp4가 아니면 INVALID_FILE_TYPE 예외를 발생시킵니다._FAIL")
    void isValidFileType() throws IOException {
        //given
        String videoType = "mp3";
        byte[] bytes = new byte[100 * 1024 * 1024];//파일 크기 101MB로 설정.
        MockMultipartFile mockMultipartFile = mockMultipartFile(bytes, videoType);

        //when & then
        CustomException customException = catchThrowableOfType(() ->
                        fileValidation.isValidFileType(mockMultipartFile),
                CustomException.class
        );
        assertThat(customException).isNotNull();
        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_FILE_TYPE);
    }

    @Test
    @DisplayName("유저의 하이라이트 영상이 아닌 경우 NOT_MATCH_HIGHLIGHT_VIDEO 에외를 발생시킵니다._FAIL")
    void isHighlightVideoSameMember() {
        //given
        UUID highlightId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();


        //when
        when(highlightQueryRepository.isMembersHighlight(memberId, highlightId))
                .thenReturn(false);

        //then
        CustomException customException = catchThrowableOfType(() ->
                        fileValidation.isHighlightVideoSameMember(highlightId, memberId),
                CustomException.class
        );
        assertThat(customException).isNotNull();
        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.NOT_MATCH_HIGHLIGHT_VIDEO);
    }


    /**
     * Mock MultiPartFile
     */
    private MockMultipartFile mockMultipartFile(byte[] size, String videoType) throws IOException {
        return new MockMultipartFile(
                "uploadHighlightDto",
                "test.mp4",
                videoType,
                size
        );
    }
}