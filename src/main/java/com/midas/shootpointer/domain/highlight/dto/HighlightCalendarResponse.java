package com.midas.shootpointer.domain.highlight.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record HighlightCalendarResponse(
        @NotNull Integer year,
        @NotNull Integer month,
        @NotNull List<CalendarDaysResponse> days
) {
    public record CalendarDaysResponse(
            @NotNull LocalDate date,
            @NotNull List<HighlightInfoResponse> highlights
            ){
    }
}

