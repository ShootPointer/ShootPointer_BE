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
     * Reset Redis WEEKLY rankings according to the configured schedule.
     *
     * <p>Runs on the cron schedule defined by {@code schedules.cron.ranking.redis.weekly} in the
     * configured time zone and removes all entries belonging to the WEEKLY ranking.
     */
    @Scheduled(cron = "${schedules.cron.ranking.redis.weekly}",zone = "${schedules.zone}")
    public void deleteAllWeeklyRanking(){
        rankingRedisRepository.deleteAll(RankingType.WEEKLY);
    }

    /**
     * Clears Redis entries for the MONTHLY ranking on the first day of each month.
     *
     * <p>Triggered by the configured monthly cron schedule; resets stored monthly ranking data in Redis.
     */
    @Scheduled(cron = "${schedules.cron.ranking.redis.monthly}",zone = "${schedules.zone}")
    public void deleteAllMonthlyRanking(){
        rankingRedisRepository.deleteAll(RankingType.MONTHLY);
    }
}