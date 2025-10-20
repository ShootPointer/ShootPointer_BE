package com.midas.shootpointer.domain.ranking.dto;

import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RankingResponse {
    RankingType rankingType;
    List<RankingEntry> rankingList;

    public RankingResponse of(RankingType type,List<RankingEntry> rankingList){
        return new RankingResponse(type,rankingList);
    }
}
