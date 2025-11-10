package com.midas.shootpointer.domain.progress.dto;

import com.midas.shootpointer.domain.progress.ProgressType;
import jakarta.validation.constraints.NotNull;

public record ProgressResponse(
        @NotNull int status,
        @NotNull boolean success,
        @NotNull double progress, //진행률
        @NotNull ProgressType type //진행 유형
        ) {
}
