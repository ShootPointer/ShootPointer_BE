package com.midas.shootpointer.domain.highlight.helper;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface HighlightValidator {
    boolean filesExist(String directory);
    void isExistHighlightId(UUID highlightId);
    void isValidMembersHighlight(UUID highlightId,UUID memberId);
    void isValidMp4File(MultipartFile file);
    void isValidFileSize(MultipartFile file);
    boolean isExistDirectory(String directory);
    void areValidFiles(List<MultipartFile> files);
}
