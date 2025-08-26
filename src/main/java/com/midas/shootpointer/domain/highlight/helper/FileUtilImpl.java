package com.midas.shootpointer.domain.highlight.helper;

import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class FileUtilImpl implements FileUtil{
    //영상 저장 경로
    @Value("${video.path}")
    private String videoPath;


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
}
