package com.midas.shootpointer.domain.ranking.mapper;

import com.midas.shootpointer.batch.dto.HighlightWithMemberDto;
import com.midas.shootpointer.domain.ranking.dto.RankingResponse;
import com.midas.shootpointer.domain.ranking.dto.RankingResult;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;

import java.util.List;

public interface RankingMapper {
    RankingEntry dtoToEntity(HighlightWithMemberDto dto);
    RankingResponse docToResponse(RankingDocument document);
    RankingResponse resultToResponse(List<RankingResult> result, RankingType type);
    RankingResponse entryToResponse(List<RankingEntry> entries,RankingType type);
    RankingResult convertToRankingResult(Object o);
}
