package com.midas.shootpointer.batch.listener.ranking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 배치 시스템 - 랭킹 리스너
 */
@Slf4j
@Component
public class RankingJobExecutionListener {

    /*
    =================== Job 실행 이전 ===================
     */

    //월간 집계 Job
    @BeforeJob
    public void monthlyRankingBeforeJob(){
        log.info("월간 하이라이트 배치 시작 {}", LocalDateTime.now());
    }

    //주간 집계 Job
    @BeforeJob
    public void weeklyRankingBeforeJob(){
        log.info("주간 하이라이트 배치 시작 {}", LocalDateTime.now());
    }

    /*
    =================== Job 실행 이후 ===================
     */

    //월간 집계 Job
    @AfterJob
    public void monthlyAfterJob(){
        log.info("월간 하이라이트 배치 종료 {}", LocalDateTime.now());
    }

    //주간 집계 Job
    @AfterJob
    public void weeklyAfterJob(){
        log.info("주간 하이라이트 배치 종료 {}", LocalDateTime.now());
    }
}
