package com.midas.shootpointer.domain.highlight.dto;

import jakarta.validation.constraints.NotNull;

public record HighlightInfo(
        @NotNull String highlightUrl,
        @NotNull Integer twoPointCount,
        @NotNull Integer threePointCount
) {
    public static HighlightInfo of(String highlightUrl,Integer twoPointCount,Integer threePointCount){
        return new HighlightInfo(highlightUrl,twoPointCount,threePointCount);
    }
}
