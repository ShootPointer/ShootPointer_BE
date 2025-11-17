package com.midas.shootpointer.domain.ranking.repository;

import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.entity.RankingEntry;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
@Repository
@Transactional(readOnly = true)
public interface RankingJpaRepository  {
    //이번 주 / 이번 달 랭킹 Top10
    List<RankingEntry> fetchThisWeekRankingTop10(LocalDateTime startDate, LocalDateTime endDate, RankingType type);
}
