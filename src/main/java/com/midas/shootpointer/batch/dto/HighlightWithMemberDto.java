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
    /**
     * 하이라이트 필드
     */
    private UUID highlightId;
    private String highlightUrl;
    private UUID highlightKey;
    private Integer twoPointTotal;
    private Integer threePointTotal;
    private Integer totalScore;

    /**
     * 멤버 필드
     */
    private UUID memberId;
    private String memberName;
}
