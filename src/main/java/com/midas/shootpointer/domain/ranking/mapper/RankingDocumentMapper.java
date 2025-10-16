package com.midas.shootpointer.domain.ranking.mapper;

import com.midas.shootpointer.batch.dto.HighlightWithMemberDto;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;

public interface RankingDocumentMapper {
    RankingEntry dtoToEntity(HighlightWithMemberDto dto);
}
