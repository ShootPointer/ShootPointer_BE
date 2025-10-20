package com.midas.shootpointer.domain.ranking.dto;

import java.util.UUID;

public record RankingResult(
        String memberName,
        UUID memberId,
        int total,
        int twoTotal,
        int threeTotal
) {
}
