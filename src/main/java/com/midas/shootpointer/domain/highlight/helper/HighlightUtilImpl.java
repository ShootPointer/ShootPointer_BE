package com.midas.shootpointer.domain.highlight.helper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.highlight.repository.HighlightCommandRepository;
import com.midas.shootpointer.domain.highlight.repository.HighlightQueryRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class HighlightUtilImpl implements HighlightUtil{
    //영상 저장 경로
    private final String videoPath;

    private final HighlightQueryRepository highlightQueryRepository;

    private final HighlightCommandRepository highlightCommandRepository;

    public HighlightUtilImpl(@Value("${video.path}") String videoPath, HighlightQueryRepository highlightQueryRepository, HighlightCommandRepository highlightCommandRepository){
        this.videoPath=videoPath;
        this.highlightQueryRepository = highlightQueryRepository;
        this.highlightCommandRepository = highlightCommandRepository;
    }

    @Override
    public String getDirectoryPath(String highlightKey) {
        String directory = videoPath + "/" + highlightKey;
        Path directoryPath = Paths.get(directory);
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                log.error("method : getDirectoryPath message : {}",e.getMessage());
                throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }
        return directoryPath.toString();
    }

    @Override
    public HighlightEntity findHighlightByHighlightId(UUID highlightId) {
        return highlightQueryRepository.findByHighlightId(highlightId)
                .orElseThrow(()->new CustomException(ErrorCode.NOT_EXIST_HIGHLIGHT));
    }

    @Override
    public List<HighlightEntity> savedAll(List<HighlightEntity> entities) {
        return highlightCommandRepository.saveAll(entities);
    }

}
