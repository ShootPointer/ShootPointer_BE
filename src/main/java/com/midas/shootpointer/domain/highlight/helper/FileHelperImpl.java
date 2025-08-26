package com.midas.shootpointer.domain.highlight.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
@RequiredArgsConstructor
@Component
public class FileHelperImpl implements FileHelper{
    private final FileUtil fileUtil;
    private final FileValidation fileValidation;

    @Override
    public String getDirectoryPath(String highlightKey) {
        return fileUtil.getDirectoryPath(highlightKey);
    }

    @Override
    public void isValidFileSize(MultipartFile file) {
        fileValidation.isValidFileSize(file);
    }

    @Override
    public void isValidFileType(MultipartFile file) {
        fileValidation.isValidFileType(file);
    }

    @Override
    public void isHighlightVideoSameMember(UUID highlightId, UUID memberId) {
        fileValidation.isHighlightVideoSameMember(highlightId,memberId);
    }
}
