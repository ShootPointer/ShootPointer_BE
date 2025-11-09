package com.midas.shootpointer.domain.highlight.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record HighlightInfoResponse(
        UUID highlightId,
        LocalDateTime createdDate,
        Integer totalTwoPoint,
        Integer totalThreePoint,
        String highlightUrl
) {
    public static HighlightInfoResponse of(UUID highlightId, LocalDateTime createdDate, Integer totalTwoPoint, Integer totalThreePoint,String highlightUrl){
        return new HighlightInfoResponse(highlightId,createdDate,totalTwoPoint,totalThreePoint,highlightUrl);
    }
}
