package com.midas.shootpointer;

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
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m")
           // .withCommand("sh", "-c", "elasticsearch-plugin install analysis-nori && elasticsearch")
            .waitingFor(Wait.forHttp("/").forStatusCode(200).withStartupTimeout(Duration.ofSeconds(90)));

    static {
        //local 에서만 test container 실행
        String mode=System.getProperty("spring.elasticsearch.mode","container");
        if("container".equalsIgnoreCase(mode)) {
            container.start();
        }
    }



    @DynamicPropertySource
    static void setElasticsearchProperties(DynamicPropertyRegistry registry){
        String mode=System.getProperty("spring.elasticsearch.mode","container");

        if ("container".equalsIgnoreCase(mode)){
            registry.add("spring.elasticsearch.uris",container::getHttpHostAddress);
        }else{
            //CI 환경 - Jacoco
            registry.add("spring.elasticsearch.uris",()->"http://localhost:9200");
        }
    }

}
