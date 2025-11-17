package com.midas.shootpointer.domain.highlight.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record PeriodHighlightResponse(
        @NotBlank String highlightUrl,
        @NotBlank UUID highlightId,
        @NotBlank Long postId,
        @NotBlank String memberName,
        @NotBlank Long likeCnt,
        @NotBlank String title
        ) {
}
