package com.midas.shootpointer.global.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories
@Profile("dev")  // dev 프로파일에서만 활성화
public class ElasticSearchConfig  {
    @Value("${spring.elasticsearch.uris}")
    private String host;

    @Bean
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(host.replace("http://",""))
                .build();
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(){
        RestClient restClient=RestClient.builder(HttpHost.create(host)).build();
        return new ElasticsearchClient(new RestClientTransport(restClient,new JacksonJsonpMapper()));
    }
}
