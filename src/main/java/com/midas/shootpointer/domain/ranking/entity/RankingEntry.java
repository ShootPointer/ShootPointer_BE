package com.midas.shootpointer.domain.ranking.entity;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RankingEntry {
    @Setter
    private Integer rank;
    private UUID memberId;
    private String memberName;
    private Integer totalScore;
    private Integer twoScore;
    private Integer threeScore;
}
