package com.midas.shootpointer.domain.highlight.helper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HighlightHelperImpl implements HighlightHelper{
    private final HighlightUtilImpl highlightUtil;
    @Override
    public String getDirectoryPath(String highlightKey) {
        return highlightUtil.getDirectoryPath(highlightKey);
    }

    @Override
    public HighlightEntity findHighlightByHighlightId(UUID highlightId) {
        return highlightUtil.findHighlightByHighlightId(highlightId);
    }

}
