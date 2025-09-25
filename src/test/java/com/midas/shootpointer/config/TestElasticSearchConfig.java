package com.midas.shootpointer.config;

import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.elasticsearch.PostElasticSearchRepository;
import com.midas.shootpointer.domain.post.elasticsearch.helper.PostElasticSearchHelper;
import com.midas.shootpointer.domain.post.elasticsearch.helper.PostElasticSearchUtil;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.Collections;
import java.util.List;

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
        return new PostElasticSearchHelper() {
            @Override
            public Long createPostDocument(PostEntity post) {
                return post.getPostId();
            }
            
            @Override
            public List<PostResponse> getPostByTitleOrContentByElasticSearch(String search, Long lastPostId, int size) {
                return Collections.emptyList();
            }
        };
    }
    
    @Bean
    @Primary
    public PostElasticSearchUtil postElasticSearchUtil() {
        return Mockito.mock(PostElasticSearchUtil.class);
    }
}
