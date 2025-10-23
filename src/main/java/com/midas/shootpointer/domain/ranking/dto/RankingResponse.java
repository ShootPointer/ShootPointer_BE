package com.midas.shootpointer.domain.ranking.dto;

import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class RankingResponse {
    RankingType rankingType;
    List<RankingEntry> rankingList;

    public static RankingResponse of(RankingType type,List<RankingEntry> rankingList){
        return new RankingResponse(type,rankingList);
    }
}
