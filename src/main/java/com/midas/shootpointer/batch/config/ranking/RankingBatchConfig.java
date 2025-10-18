package com.midas.shootpointer.batch.config.ranking;

import com.midas.shootpointer.batch.dto.HighlightWithMemberDto;
import com.midas.shootpointer.batch.listener.ranking.RankingJobExecutionListener;
import com.midas.shootpointer.batch.listener.ranking.RankingStepExecutionListener;
import com.midas.shootpointer.batch.processor.ranking.RankingProcessor;
import com.midas.shootpointer.batch.reader.ranking.RankingReader;
import com.midas.shootpointer.batch.validator.RankingValidator;
import com.midas.shootpointer.batch.writer.ranking.RankingWriter;
import com.midas.shootpointer.domain.ranking.entity.RankingDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class RankingBatchConfig extends DefaultBatchConfiguration {
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverName;

    private final JobRepository jobRepository;
    private final RankingReader rankingItemReader;
    private final RankingProcessor rankingProcessor;
    private final RankingWriter rankingWriter;
    private final RankingValidator validator;

    /**
     * Page Size
     */
    private final int PAGE_SIZE=1_000;


    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource driverManagerDataSource=new DriverManagerDataSource();
        driverManagerDataSource.setUrl(url);
        driverManagerDataSource.setUsername(userName);
        driverManagerDataSource.setPassword(password);
        driverManagerDataSource.setDriverClassName(driverName);

        return driverManagerDataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(){
        return new DataSourceTransactionManager(dataSource());
    }

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
                .<HighlightWithMemberDto, RankingDocument>chunk(PAGE_SIZE,transactionManager())
                .listener(new RankingStepExecutionListener())
                .reader(rankingItemReader)
                .processor(rankingProcessor)
                .writer(rankingWriter)
                .build();
    }

}


