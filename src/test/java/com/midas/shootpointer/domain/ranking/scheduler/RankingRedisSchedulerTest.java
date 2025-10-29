package com.midas.shootpointer.domain.ranking.scheduler;

import com.midas.shootpointer.BaseSpringBootTest;
import com.midas.shootpointer.domain.ranking.repository.RankingRedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@Import(RankingRedisScheduler.class)
@ActiveProfiles("test")
class RankingRedisSchedulerTest extends BaseSpringBootTest {
    @MockitoSpyBean
    private RankingRedisScheduler scheduler;

    @MockitoSpyBean
    private RankingRedisRepository redisRepository;

    @Test
    @DisplayName("스케줄러가 실제로 트리거되어 RankingRedisRepository의 deleteAll을 호출합니다.")
    void schedulerTasks(){
        //주간 배치 스케줄러
        await()
                .atMost(Duration.ofSeconds(4))
                .untilAsserted(() ->
                        verify(scheduler, atLeastOnce()).deleteAllWeeklyRanking()
                );

        //월간 배치 스케줄러
        await()
                .atMost(Duration.ofSeconds(4))
                .untilAsserted(() ->
                        verify(scheduler, atLeastOnce()).deleteAllMonthlyRanking()
                );

        verify(redisRepository, atLeastOnce()).deleteAll(any());

    }
}