package com.midas.shootpointer.batch.listener.ranking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.stereotype.Component;
/**
 * 배치 시스템 - 랭킹 리스너
 */
@Slf4j
@Component
public class RankingStepExecutionListener {
     /*
    =================== Step 실행 이전 ===================
     */

    /**
     *  [ 월간 Step ]
     */


    /**
     *  [ 주간 Step ]
     */



    /*
    =================== Step 실행 이후 ===================
     */

    /**
     *  [ 월간 Step ]
     */

    //데이터 불러오기
    @AfterStep
    public ExitStatus fetchMonthlyHighlight(StepExecution stepExecution){
        log.info("");
        return new ExitStatus("");
    }

    /**
     *  [ 주간 Step ]
     */



}
