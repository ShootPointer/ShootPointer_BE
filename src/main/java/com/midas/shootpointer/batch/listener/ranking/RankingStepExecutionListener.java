package com.midas.shootpointer.batch.listener.ranking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 배치 시스템 - 랭킹 리스너
 */
@Slf4j
@Component
public class RankingStepExecutionListener {
     /*
    =================== Step 실행 이전 ===================
     */
    @BeforeStep
    public void rankingBeforeStep(StepExecution stepExecution){
        log.info("""
                 =========== [START] 하이라이트 배치 Step ===========
                 Step name       :{} 
                 createdTime     :{}          
                 Job ExecutionId :{}
                 ================================================
                 """,
                stepExecution.getStepName(),
                stepExecution.getCreateTime(),
                stepExecution.getJobExecutionId()
        );
    }


    /*
    =================== Step 실행 이후 ===================
     */

    //데이터 불러오기
    @AfterStep
    public ExitStatus fetchMonthlyHighlight(StepExecution stepExecution){
        LocalDateTime start=stepExecution.getStartTime();
        LocalDateTime end=stepExecution.getEndTime();

        long durationMs=(start!=null && end!=null) ?
                Duration.between(start,end).toMillis() :
                -1L;

        log.info("""
                 =========== [END] 하이라이트 배치 Step ============
                 Step name      :{}
                 Status         :{}
                 Read Count     :{}
                 Write Count    :{}
                 Commit Count   :{}
                 Skip Count     :{}
                 Rollback Count :{}
                 Exit Status    :{}
                 Duration(ms)   :{}
                 ================================================
                 """,
                stepExecution.getStepName(),
                stepExecution.getStatus(),
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getCommitCount(),
                stepExecution.getSkipCount(),
                stepExecution.getRollbackCount(),
                stepExecution.getExitStatus(),
                durationMs
        );
        return stepExecution.getExitStatus();
    }
}
