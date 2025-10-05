package com.midas.shootpointer;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

@Profile({"es","test"})
@Testcontainers
@TestConfiguration(proxyBeanMethods = false)
public class ElasticSearchTestContainer {
    private final static String IMAGE_NAME="docker.elastic.co/elasticsearch/elasticsearch:8.6.0";

    @Container
    private static final ElasticsearchContainer container=new ElasticsearchContainer(IMAGE_NAME)
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("xpack.security.http.ssl.enabled", "false")
            .withCommand("sh", "-c", "elasticsearch-plugin install analysis-nori && elasticsearch")
            .waitingFor(Wait.forHttp("/").forStatusCode(200).withStartupTimeout(Duration.ofSeconds(90)));;


    @DynamicPropertySource
    static void setElasticsearchProperties(DynamicPropertyRegistry registry){
        registry.add("spring.elasticsearch.uris",container::getHttpHostAddress);
    }

}
