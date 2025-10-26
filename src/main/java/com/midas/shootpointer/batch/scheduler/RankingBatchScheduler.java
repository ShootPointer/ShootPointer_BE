package com.midas.shootpointer.batch.scheduler;

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
 * 랭킹 스케쥴러
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
     * 매일 자정 하루 랭킹 배치
     */
    @Scheduled(cron = "0 0 0 * * *",zone = "Asia/Seoul")
    public void batchDailyRankingJob()  {
        runRankingJob(RankingType.DAILY);
    }

    /**
     * 매주 일요일 자정 랭킹 배치 살행
     */
    @Scheduled(cron = "0 0 0 * * MON",zone = "Asia/Seoul")
    public void batchWeeklyRankingJob(){
        runRankingJob(RankingType.WEEKLY);
    }

    /**
     * 매월 1일 자정 랭킹 배치 실행
     */
    @Scheduled(cron = "0 0 0 1 * *",zone = "Asia/Seoul")
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
