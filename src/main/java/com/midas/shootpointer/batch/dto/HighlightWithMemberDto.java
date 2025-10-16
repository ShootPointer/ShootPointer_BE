package com.midas.shootpointer.batch.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class HighlightWithMemberDto {
    private UUID highlightId;
    private String highlightUrl;
    private UUID highlightKey;
    private Boolean isSelected;
    private Integer twoPointCount;
    private Integer threePointCount;

    private UUID memberId;
    private String memberName;
    private Boolean agreeToAggregation;
}
