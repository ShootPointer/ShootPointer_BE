package com.midas.shootpointer.domain.highlight.mapper;

import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;

import java.util.List;
import java.util.UUID;

public interface HighlightMapper {
    HighlightSelectResponse entityToResponse(List<UUID> selectedHighlights);
}
