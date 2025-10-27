package com.midas.shootpointer.domain.ranking.scheduler;

import com.midas.shootpointer.domain.ranking.dto.RankingType;
import com.midas.shootpointer.domain.ranking.repository.RankingRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *  랭킹 - Redis 스켸줄러
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
public class RankingRedisScheduler {
    private final RankingRedisRepository rankingRedisRepository;

    /**
     * 매주 월요일 자정 랭킹 - Redis WEEKLY 초기화
     */
    @Scheduled(cron = "0 0 0 * * MON",zone = "Asia/Seoul")
    public void deleteAllWeeklyRanking(){
        rankingRedisRepository.deleteAll(RankingType.WEEKLY);
    }

    /**
     * 매주 1일 랭킹 - Redis MONTHLY 초기화
     */
    @Scheduled(cron = "0 0 0 1 * *",zone = "Asia/Seoul")
    public void deleteAllMonthlyRanking(){
        rankingRedisRepository.deleteAll(RankingType.MONTHLY);
    }
}
