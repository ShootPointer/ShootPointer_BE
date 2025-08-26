package com.midas.shootpointer.domain.highlight.helper;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileValidation {
    void isValidFileSize(MultipartFile file);
    void isValidFileType(MultipartFile file);
    void isHighlightVideoSameMember(UUID highlightId,UUID memberId);
}
