package com.midas.shootpointer.domain.highlight.helper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HighlightHelperImpl implements HighlightHelper{
    private final HighlightUtilImpl highlightUtil;
    private final HighlightValidator highlightValidator;
    @Override
    public String getDirectoryPath(String highlightKey) {
        return highlightUtil.getDirectoryPath(highlightKey);
    }

    @Override
    public HighlightEntity findHighlightByHighlightId(UUID highlightId) {
        return highlightUtil.findHighlightByHighlightId(highlightId);
    }

    @Override
    public List<HighlightEntity> savedAll(List<HighlightEntity> entities) {
        return highlightUtil.savedAll(entities);
    }

    @Override
    public boolean filesExist(String directory) {
        return highlightValidator.filesExist(directory);
    }

    @Override
    public void isExistHighlightId(UUID highlightId) {
        highlightValidator.isExistHighlightId(highlightId);
    }

    @Override
    public void isValidMembersHighlight(UUID highlightId, UUID memberId) {
        highlightValidator.isValidMembersHighlight(highlightId,memberId);
    }

    @Override
    public void isValidMp4File(MultipartFile file) {
        highlightValidator.isValidMp4File(file);
    }

    @Override
    public void isValidFileSize(MultipartFile file) {
        highlightValidator.isValidMp4File(file);
    }

    @Override
    public boolean isExistDirectory(String directory) {
        return highlightValidator.isExistDirectory(directory);
    }

    @Override
    public void areValidFiles(List<MultipartFile> files) {

    }
}
