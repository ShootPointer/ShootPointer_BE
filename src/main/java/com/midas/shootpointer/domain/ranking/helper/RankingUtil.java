package com.midas.shootpointer.domain.ranking.helper;

import com.midas.shootpointer.domain.ranking.dto.RankingResult;
import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface RankingUtil {
    RankingDocument fetchRankingDocumentByPeriodKey(String periodKey);
    String getRankingTypeKey(RankingType type,LocalDateTime now);
    List<RankingResult> fetchRankingResult(LocalDateTime start,LocalDateTime end) throws IOException;
}
