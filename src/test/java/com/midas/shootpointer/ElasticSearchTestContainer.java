package com.midas.shootpointer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@Profile({"es","test"})
@Testcontainers
@TestConfiguration(proxyBeanMethods = false)
@ConditionalOnExpression("!'${SPRING_ELASTICSEARCH_MODE:local}'.equals('external')")
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
            )
            .withLogConsumer(outputFrame -> {
                String log = outputFrame.getUtf8String();
                if (outputFrame.getType() == OutputFrame.OutputType.STDERR ||
                    (log != null && log.contains("ERROR"))) {
                    System.err.print("[Elasticsearch ERROR] " + log);
                }
            });

    static {
        System.out.println("========== [ElasticSearchTestContainer] Starting ==========");
        container.start();
        System.out.println("========== [ElasticSearchTestContainer] Started at: "
                           + container.getHttpHostAddress() + " ==========");
    }



    @DynamicPropertySource
    static void setElasticsearchProperties(DynamicPropertyRegistry registry){
        registry.add("spring.elasticsearch.uris",container::getHttpHostAddress);
    }

}
