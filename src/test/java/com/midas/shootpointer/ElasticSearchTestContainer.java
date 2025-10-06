package com.midas.shootpointer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@Profile({"es","test"})
@Testcontainers
@TestConfiguration(proxyBeanMethods = false)
@ConditionalOnProperty(
        name = "spring.elasticsearch.mode",
        havingValue = "local",
        matchIfMissing = true  // 환경변수가 없으면 local로 간주 (기본값)
)
@Slf4j
public class ElasticSearchTestContainer {
    private static final DockerImageName IMAGE_NAME =
            DockerImageName.parse("tkv00/elasticsearch-nori:7.17.9")
                    .asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch");

    @Container
    private static final ElasticsearchContainer container=new ElasticsearchContainer(IMAGE_NAME)
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("xpack.security.http.ssl.enabled", "false")
            .withEnv("bootstrap.memory_lock", "false")
            .withEnv("ES_JAVA_OPTS", "-Xms256m -Xmx256m")
            //.withCommand("sh", "-c", "elasticsearch-plugin install analysis-nori && elasticsearch");
            .waitingFor(
            Wait.forHttp("/")
                            .forStatusCode(200)
                            .withStartupTimeout(Duration.ofSeconds(180))
            );

    static {
        log.info("========== [ElasticSearchTestContainer] Starting ==========");
        try {
            container.start();
            log.info("========== [ElasticSearchTestContainer] Started at: {} ==========", container.getHttpHostAddress());
        } catch (Exception e) {
            log.error("Failed to start Elasticsearch container: {}", e.getMessage(), e);
            throw e;
        }
    }



    @DynamicPropertySource
    static void setElasticsearchProperties(DynamicPropertyRegistry registry){
        registry.add("spring.elasticsearch.uris",container::getHttpHostAddress);
    }

}
