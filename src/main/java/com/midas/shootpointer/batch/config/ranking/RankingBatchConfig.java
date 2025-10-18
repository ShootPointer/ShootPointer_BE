package com.midas.shootpointer.batch.config.ranking;

import com.midas.shootpointer.batch.dto.HighlightWithMemberDto;
import com.midas.shootpointer.batch.listener.ranking.RankingJobExecutionListener;
import com.midas.shootpointer.batch.listener.ranking.RankingStepExecutionListener;
import com.midas.shootpointer.batch.processor.ranking.RankingProcessor;
import com.midas.shootpointer.batch.validator.RankingValidator;
import com.midas.shootpointer.batch.writer.ranking.RankingWriter;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class RankingBatchConfig {
    private final JobRepository jobRepository;
    private final RankingProcessor rankingProcessor;
    private final JdbcPagingItemReader<HighlightWithMemberDto> highlightReader;
    private final RankingWriter rankingWriter;
    private final RankingValidator validator;
    private final PlatformTransactionManager transactionManager;
    /**
     * Page Size
     */
    private final int PAGE_SIZE=1_000;

    @Bean
    public Job rankingJob(){
        return new JobBuilder("RankingJob",jobRepository)
                .listener(new RankingJobExecutionListener())
                .validator(validator)
                .start(rankingStep())
                .build();
    }

    @Bean
    public Step rankingStep(){
        return new StepBuilder("RankingStep",jobRepository)
                .<HighlightWithMemberDto, RankingDocument>chunk(PAGE_SIZE,transactionManager)
                .listener(new RankingStepExecutionListener())
                .reader(highlightReader)
                .processor(rankingProcessor)
                .writer(rankingWriter)
                .build();
    }

}


