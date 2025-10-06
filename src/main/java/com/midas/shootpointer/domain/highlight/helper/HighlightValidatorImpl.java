package com.midas.shootpointer.domain.highlight.helper;

import com.midas.shootpointer.domain.highlight.repository.HighlightQueryRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HighlightValidatorImpl implements HighlightValidator{
    /**
     * 영상 크기 제한 500MB
     */
    private static final long MAX_FILE_SIZE = 500L * 1024L * 1024L;
    private final HighlightQueryRepository highlightQueryRepository;

    @Override
    public boolean filesExist(String directory) {
        Path directoryPath= Paths.get(directory);
        return Files.exists(directoryPath);
    }

    @Override
    public void isExistHighlightId(UUID highlightId) {
        if (!highlightQueryRepository.existsByHighlightId(highlightId)){
            throw new CustomException(ErrorCode.NOT_EXIST_HIGHLIGHT);
        }
    }

    @Override
    public void isValidMembersHighlight(UUID highlightId, UUID memberId) {
        if (!highlightQueryRepository.isMembersHighlight(memberId,highlightId)){
            throw new CustomException(ErrorCode.NOT_MATCH_HIGHLIGHT_VIDEO);
        }
    }


    @Override
    public void isValidMp4File(MultipartFile file) {
        String contentType=file.getContentType();
        if(!contentType.equals("video/mp4")){
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    @Override
    public void isValidFileSize(MultipartFile file) {
        long fileSize=file.getSize();
        if (fileSize>=MAX_FILE_SIZE){
            throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
        }
    }

    @Override
    public boolean isExistDirectory(String directory) {
        return Files.exists(Paths.get(directory));
    }
}
