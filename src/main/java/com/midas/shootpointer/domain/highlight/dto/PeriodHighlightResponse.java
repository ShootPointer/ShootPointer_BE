package com.midas.shootpointer.domain.highlight.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PeriodHighlightResponse(
        @NotBlank String highlightUrl,
        @NotNull UUID highlightId,
        @NotNull Long postId,
        @NotBlank String username,
        @NotNull Long likeCnt,
        @NotNull Long periodLikeCnt,
        @NotBlank String title
        ) {
}
