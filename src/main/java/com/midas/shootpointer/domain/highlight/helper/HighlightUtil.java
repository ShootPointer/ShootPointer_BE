package com.midas.shootpointer.domain.highlight.helper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;

import java.util.UUID;

public interface HighlightUtil {
    String getDirectoryPath(String highlightKey);
    HighlightEntity findHighlightByHighlightId(UUID highlightId);
}
