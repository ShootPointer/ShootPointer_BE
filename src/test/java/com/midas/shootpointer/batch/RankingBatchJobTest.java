package com.midas.shootpointer.batch;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.springframework.batch.core.Job;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * ranking batch 통합 테스트
 */
@ActiveProfiles("test")
@SpringBootTest
@SpringBatchTest
public class RankingBatchJobTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Job rankingJob;

    @PostConstruct
    public void configureJobLauncherTestUtils() throws Exception{
        jobLauncherTestUtils.setJob(rankingJob);
    }

    @AfterEach
    void cleanUp(){
        jdbcTemplate.execute("");
    }
}
