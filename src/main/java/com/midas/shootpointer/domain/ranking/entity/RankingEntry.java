package com.midas.shootpointer.domain.ranking.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RankingEntry {
    private int rank;
    private UUID memberId;
    private String memberName;
    private int score;
}
