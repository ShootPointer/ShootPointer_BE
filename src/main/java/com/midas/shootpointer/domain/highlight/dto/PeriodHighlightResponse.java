package com.midas.shootpointer.domain.highlight.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record PeriodHighlightResponse(
        @NotBlank String highlightUrl,
        @NotBlank UUID highlightId,
        @NotBlank Long postId,
        @NotBlank String username,
        @NotBlank Long likeCnt,
        @NotBlank Long periodLikeCnt,
        @NotBlank String title
        ) {
}
