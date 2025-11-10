package com.midas.shootpointer.domain.progress.dto;

import jakarta.validation.constraints.NotNull;

public record SseEvent(
        @NotNull long eventId,
        @NotNull String name,
        @NotNull Object data
) {
}
