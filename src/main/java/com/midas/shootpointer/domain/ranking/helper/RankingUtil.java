package com.midas.shootpointer.domain.ranking.helper;

import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;

import java.time.LocalDateTime;

public interface RankingUtil {
    RankingDocument fetchRankingDocumentByPeriodKey(String periodKey);
    String getRankingTypeKey(RankingType type,LocalDateTime now);
}
