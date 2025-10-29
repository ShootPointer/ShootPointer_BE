package com.midas.shootpointer.domain.ranking.scheduler;

import com.midas.shootpointer.domain.ranking.dto.RankingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 랭킹 - batch 스케쥴러
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
@Profile("batch")
@Slf4j
public class RankingBatchScheduler {
    private final JobLauncher jobLauncher;
    private final Job rankingJob;
    private final DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Triggers the batch job that computes daily rankings.
     *
     * The job is scheduled according to the configured cron expression and time zone to run once per day.
     */
    @Scheduled(cron = "${schedules.cron.ranking.batch.daily}",zone = "${schedules.zone}")
    public void batchDailyRankingJob()  {
        runRankingJob(RankingType.DAILY);
    }

    /**
     * Triggers the weekly ranking batch job.
     *
     * Scheduled according to the configured cron expression `${schedules.cron.ranking.batch.weekly}` and time zone `${schedules.zone}`.
     */
    @Scheduled(cron = "${schedules.cron.ranking.batch.weekly}",zone = "${schedules.zone}")
    public void batchWeeklyRankingJob(){
        runRankingJob(RankingType.WEEKLY);
    }

    /**
     * Starts the monthly ranking batch job scheduled to run at midnight on the first day of each month.
     */
    @Scheduled(cron = "${schedules.cron.ranking.batch.monthly}",zone = "${schedules.zone}")
    public void batchMonthlyRankingJob(){
        runRankingJob(RankingType.MONTHLY);
    }

    private void runRankingJob(RankingType type)  {
        LocalDateTime end=LocalDateTime.now();
        String endStr=end.format(formatter);

        try {
            JobParameters jobParameters=new JobParametersBuilder()
                    .addString("rankingType",type.name())
                    .addString("end",endStr)
                    .toJobParameters();

            jobLauncher.run(rankingJob,jobParameters);
        }catch (Exception e){
            log.error("Ranking job 배치 실행 오류 msg : {} time : {}",e.getMessage(),LocalDateTime.now());
        }
    }
}