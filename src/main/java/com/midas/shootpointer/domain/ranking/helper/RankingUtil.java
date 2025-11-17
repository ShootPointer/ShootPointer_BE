package com.midas.shootpointer.domain.ranking.helper;

import com.midas.shootpointer.domain.ranking.dto.RankingResult;
import com.midas.shootpointer.domain.ranking.entity.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface RankingUtil {
    RankingDocument fetchRankingDocumentByPeriodKey(String periodKey);
    String getRankingTypeKey(RankingType type,LocalDateTime now);
    List<RankingResult> fetchRankingResult(LocalDateTime start,LocalDateTime end) throws IOException;
    LocalDateTime getBeginTime(LocalDateTime end,RankingType type);
    double calculateRankingWeight(int twoScore,int threeScore,int totalScore);
    List<RankingEntry> calculateRanking(List<RankingEntry> origin);
    LocalDateTime calculateStartDate(LocalDateTime now,RankingType type);
    LocalDateTime calculateEndDate(LocalDateTime now,RankingType type);
}
