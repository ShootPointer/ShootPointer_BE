package com.midas.shootpointer.domain.highlight.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record HighlightCalendarDaysResponse(
        @NotNull LocalDate date,
        @NotNull Integer count,
        @NotNull List<HighlightInfoResponse> highlights
) {
}
