package com.midas.shootpointer;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

@Testcontainers
@TestConfiguration(proxyBeanMethods = false)
public class RedisTestContainer {
    private static final String REDIS_IMAGE="redis:7-alpine";
    private static final GenericContainer REDIS_CONTAINER;

    static {
        REDIS_CONTAINER=new GenericContainer<>(REDIS_IMAGE)
                .withExposedPorts(6379)
                .withReuse(true)
                .waitingFor(Wait.forListeningPort())
                .withStartupTimeout(Duration.ofSeconds(60));
        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry){
        registry.add("spring.data.redis.host",REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port",
                ()->String.valueOf(REDIS_CONTAINER.getMappedPort(6379)));
    }
}
