package com.midas.shootpointer.domain.highlight.service;

import com.midas.shootpointer.domain.highlight.helper.HighlightHelper;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HighlightStorageService {
    private final HighlightHelper highlightHelper;

    public List<String> storeHighlights(List<MultipartFile> highlights, String highlightKey) {
        String directoryPath = highlightHelper.getDirectoryPath(highlightKey);
        List<String> fileNames = new ArrayList<>();

        for (MultipartFile file : highlights) {
            String fileName = UUID.randomUUID() + ".mp4";
            Path filePath = Paths.get(directoryPath, fileName);
            try (OutputStream os = Files.newOutputStream(filePath)) {
                os.write(file.getBytes());
                fileNames.add(fileName);
            } catch (IOException e) {
                log.error("파일 저장 실패: {}", e.getMessage());
                throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }

        return fileNames;
    }
}
