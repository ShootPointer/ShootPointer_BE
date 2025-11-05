package com.midas.shootpointer.domain.highlight.mapper;

import com.midas.shootpointer.domain.highlight.dto.HighlightInfoResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class HighlightMapperImpl implements HighlightMapper{

    @Override
    public HighlightSelectResponse entityToResponse(List<UUID> selectedHighlights) {
        return HighlightSelectResponse.builder()
                .selectedHighlightIds(selectedHighlights)
                .build();
    }

    @Override
    public HighlightResponse entityToResponse(HighlightEntity highlight) {
        return HighlightResponse.builder()
                .createdAt(highlight.getCreatedAt())
                .highlightId(highlight.getHighlightId())
                .highlightIdentifier(highlight.getHighlightKey())
                .highlightUrl(highlight.getHighlightURL())
                .build();
    }

    @Override
    public HighlightInfoResponse infoResponseToEntity(HighlightEntity entity) {
        return HighlightInfoResponse.of(entity.getHighlightId(),entity.getCreatedAt(),entity.totalTwoPoint(),entity.getThreePointCount());
    }


}
