package com.midas.shootpointer.config;

import com.midas.shootpointer.domain.post.elasticsearch.PostElasticSearchRepository;
import com.midas.shootpointer.domain.post.elasticsearch.helper.PostElasticSearchHelper;
import com.midas.shootpointer.domain.post.elasticsearch.helper.PostElasticSearchUtil;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TestElasticSearchConfig {
    
    @Bean
    @Primary
    public PostElasticSearchRepository postElasticSearchRepository() {
        return Mockito.mock(PostElasticSearchRepository.class);
    }
    
    @Bean
    @Primary
    public PostElasticSearchHelper postElasticSearchHelper() {
        return Mockito.mock(PostElasticSearchHelper.class);
    }
    
    @Bean
    @Primary
    public PostElasticSearchUtil postElasticSearchUtil() {
        return Mockito.mock(PostElasticSearchUtil.class);
    }
}
