package com.midas.shootpointer.batch.listener.ranking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.stereotype.Component;

/**
 * 배치 시스템 - 랭킹 리스너
 */
@Slf4j
@Component
public class RankingJobExecutionListener {

    /*
    =================== Job 실행 이전 ===================
     */

    @BeforeJob
    public void rankingBeforeJob(JobExecution jobExecution){
        log.info("=========== [START] 하이라이트 배치 Job =========== \n" +
                 "Job name : {} \n" +
                 "Job Id : {} \n"+
                 "start date : {} \n" +
                 "parameter : {}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getJobInstance().getInstanceId(),
                jobExecution.getJobParameters().getDate("end"),
                jobExecution.getJobParameters()
        );
    }

    /*
    =================== Job 실행 이후 ===================
     */

    @AfterJob
    public void rankingAfterJob(JobExecution jobExecution){
        log.info("=========== [END] 하이라이트 배치 Job =========== \n" +
                 "Job name : {} \n" +
                 "Job Id : {} \n"+
                 "start date : {} \n" ,
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getJobInstance().getInstanceId(),
                jobExecution.getJobParameters().getDate("end")
        );
    }

}
