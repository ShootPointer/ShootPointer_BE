package com.midas.shootpointer.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories
@Profile("!test")  // test 프로파일이 아닐 때만 활성화
public class ElasticSearchConfig  {
    @Value("${spring.elasticsearch.uris}")
    private String host;

    @Bean
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(host.replace("http://",""))
                .build();
    }
}
