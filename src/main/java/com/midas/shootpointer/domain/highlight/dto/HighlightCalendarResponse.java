package com.midas.shootpointer.domain.highlight.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record HighlightCalendarResponse(
        @NotNull Integer year,
        @NotNull Integer month,
        @NotNull List<HighlightCalendarDaysResponse> days
) {
}

