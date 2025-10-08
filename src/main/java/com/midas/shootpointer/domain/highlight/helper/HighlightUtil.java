package com.midas.shootpointer.domain.highlight.helper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;

import java.util.List;
import java.util.UUID;

public interface HighlightUtil {
    String getDirectoryPath(String highlightKey);
    HighlightEntity findHighlightByHighlightId(UUID highlightId);
    List<HighlightEntity> savedAll(List<HighlightEntity> entities);
}
