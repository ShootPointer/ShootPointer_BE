package com.midas.shootpointer.domain.highlight.mapper;

import com.midas.shootpointer.domain.highlight.dto.HighlightInfoResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;

import java.util.List;
import java.util.UUID;

public interface HighlightMapper {
    HighlightSelectResponse entityToResponse(List<UUID> selectedHighlights);
    HighlightInfoResponse infoResponseToEntity(HighlightEntity entity);
}
