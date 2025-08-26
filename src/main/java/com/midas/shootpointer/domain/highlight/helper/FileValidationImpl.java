package com.midas.shootpointer.domain.highlight.helper;

import com.midas.shootpointer.domain.highlight.repository.HighlightQueryRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
@Component
@RequiredArgsConstructor
public class FileValidationImpl implements FileValidation{
    private final HighlightQueryRepository highlightQueryRepository;

    /*
     영상 크기 제한 100MB
  */
    private static final long MAX_FILE_SIZE = 100L * 1024L * 1024L;


    /**
     * 영상 타입
     */
    @Value("${video.type}")
    private String videoType;


    /**
     * 파일 타입 체크 메서드
     *
     * @param file 파일
     */
    @Override
    public void isValidFileSize(MultipartFile file) {
        long fileSize = file.getSize();
        //파일 사이즈 제한 - 100MB
        if (fileSize > MAX_FILE_SIZE) {
            throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
        }
    }


    /**
     * 파일 사이즈 체크 메서드
     *
     * @param file 파일
     */
    @Override
    public void isValidFileType(MultipartFile file) {
        String contentType = file.getContentType();

        //mp4 파일 형식 검사.
        if (!contentType.equals(videoType)) {
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    /**
     * 유저의 하이라이트 영상인지 확인 메서드
     *
     * @param highlightId 하이라이트 id
     * @param memberId    유저의 Id
     */
    @Override
    public void isHighlightVideoSameMember(UUID highlightId, UUID memberId) {
        if (!highlightQueryRepository.isMembersHighlight(memberId, highlightId)) {
            throw new CustomException(ErrorCode.NOT_MATCH_HIGHLIGHT_VIDEO);
        }
    }
}
