package com.midas.shootpointer.domain.ranking.scheduler;

import com.midas.shootpointer.BaseSpringBootTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@Import(RankingBatchScheduler.class)
@ActiveProfiles({"batch", "test"})
class RankingBatchSchedulerTest extends BaseSpringBootTest {
    @MockitoSpyBean
    private RankingBatchScheduler scheduler;

    @MockitoSpyBean
    private JobLauncher jobLauncher;

    @MockitoSpyBean
    private Job rankingJob;

    @Test
    @DisplayName("스케줄러가 실제로 트리거되어 JobLauncher를 호출합니다.")
    void schedulerTasks() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        //일일 배치 스케줄러
        await()
                .atMost(Duration.ofSeconds(4))
                .untilAsserted(() ->
                        verify(scheduler, atLeastOnce()).batchDailyRankingJob()
                );

        //주간 배치 스케줄러
        await()
                .atMost(Duration.ofSeconds(4))
                .untilAsserted(() ->
                        verify(scheduler, atLeastOnce()).batchWeeklyRankingJob()
                );

        //월간 배치 스케줄러
        await()
                .atMost(Duration.ofSeconds(4))
                .untilAsserted(() ->
                        verify(scheduler, atLeastOnce()).batchMonthlyRankingJob()
                );

        verify(jobLauncher, atLeastOnce()).run(eq(rankingJob), any());
    }
}