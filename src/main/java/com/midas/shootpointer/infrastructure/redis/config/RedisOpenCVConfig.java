package com.midas.shootpointer.infrastructure.redis.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.midas.shootpointer.infrastructure.redis.subscriber.ProgressSubscriber;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("prod")
public class RedisOpenCVConfig {

    @Value("${spring.data.redis.opencv.host:localhost}")
    private String host;

    @Value("${spring.data.redis.opencv.port:6379}")
    private int port;

    @Value("${spring.data.redis.opencv.channels.highlight:opencv-progress-highlight}")
    private String highlightChannel;

    @Value("${spring.data.redis.opencv.channels.upload:opencv-progress-upload}")
    private String uploadChannel;

    @PostConstruct
    public void init() {
        log.info("╔════════════════════════════════════════════════════════╗");
        log.info("║         RedisOpenCVConfig Initialization               ║");
        log.info("╠════════════════════════════════════════════════════════╣");
        log.info("║ Host:             {}:{}", host, port);
        log.info("║ Upload Channel:   {}:*", uploadChannel);
        log.info("║ Highlight Channel: {}:*", highlightChannel);
        log.info("╚════════════════════════════════════════════════════════╝");
    }

    @Bean(name = "opencvRedisConnectionFactory")
    public RedisConnectionFactory opencvRedisConnectionFactory() {
        log.info("[Redis OpenCV] Creating connection factory");

        try {
            // Redis 서버 설정
            RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
            configuration.setHostName(host);
            configuration.setPort(port);

            // Lettuce 클라이언트 설정 (타임아웃, 재연결 등)
            SocketOptions socketOptions = SocketOptions.builder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .keepAlive(true)
                    .build();

            ClientOptions clientOptions = ClientOptions.builder()
                    .socketOptions(socketOptions)
                    .autoReconnect(true)
                    .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                    .build();

            LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                    .clientOptions(clientOptions)
                    .commandTimeout(Duration.ofSeconds(10))
                    .build();

            LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration, clientConfig);

            // CRITICAL: afterPropertiesSet() 호출 필수!
            factory.afterPropertiesSet();

            log.info("[Redis OpenCV] Connection factory created");

            // 연결 테스트 - 명시적으로 수행
            log.info("[Redis OpenCV] Testing connection to {}:{}...", host, port);

            try (RedisConnection connection = factory.getConnection()) {
                String pingResult = connection.ping();
                log.info("[Redis OpenCV] ✓ Connection test SUCCESS: {}", pingResult);
                log.info("[Redis OpenCV] ✓ Redis server is reachable and responding");
            } catch (Exception e) {
                log.error("[Redis OpenCV] ✗ Connection test FAILED", e);
                log.error("[Redis OpenCV] Error details: {}", e.getMessage());
                log.error("[Redis OpenCV] Please check:");
                log.error("[Redis OpenCV]   1. Redis server is running on {}:{}", host, port);
                log.error("[Redis OpenCV]   2. Network connectivity (nc -zv {} {})", host, port);
                log.error("[Redis OpenCV]   3. Redis bind configuration (should be 0.0.0.0)");
                log.error("[Redis OpenCV]   4. Firewall allows port {}", port);
                throw new RuntimeException("Failed to connect to OpenCV Redis server", e);
            }

            return factory;

        } catch (Exception e) {
            log.error("[Redis OpenCV] Failed to create connection factory", e);
            throw new RuntimeException("Failed to initialize OpenCV Redis connection", e);
        }
    }

    @Bean(name = "opencvRedisTemplate")
    public RedisTemplate<String, Object> opencvRedisTemplate(
            @Qualifier("opencvRedisConnectionFactory") RedisConnectionFactory connectionFactory
    ) {
        log.info("[Redis OpenCV] Creating RedisTemplate...");

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper objectMapper = new ObjectMapper()
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                .registerModule(new JavaTimeModule());

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        template.afterPropertiesSet();

        log.info("[Redis OpenCV] ✓ RedisTemplate created successfully");
        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @Qualifier("opencvRedisConnectionFactory") RedisConnectionFactory connectionFactory,
            ProgressSubscriber progressSubscriber
    ) {
        log.info("[Redis OpenCV] Creating message listener container...");

        try {
            RedisMessageListenerContainer container = new RedisMessageListenerContainer();
            container.setConnectionFactory(connectionFactory);

            // 리스너 어댑터 생성
            MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(progressSubscriber);
            listenerAdapter.afterPropertiesSet();

            // Upload 채널 패턴 구독
            String uploadPattern = uploadChannel + ":*";
            PatternTopic uploadTopic = new PatternTopic(uploadPattern);
            container.addMessageListener(listenerAdapter, uploadTopic);
            log.info("[Redis OpenCV] ✓ Registered listener for pattern: {}", uploadPattern);

            // Highlight 채널 패턴 구독
            String highlightPattern = highlightChannel + ":*";
            PatternTopic highlightTopic = new PatternTopic(highlightPattern);
            container.addMessageListener(listenerAdapter, highlightTopic);

            log.info("[Redis OpenCV] ✓ Registered listener for pattern: {}", highlightPattern);
            log.info("[Redis OpenCV] ✓ Message listener container started successfully");
            log.info("[Redis OpenCV] ✓ Now listening for messages from OpenCV server...");
            log.info("[Redis OpenCV] ✓ Subscriptions active:");
            log.info("[Redis OpenCV]   - {}", uploadPattern);
            log.info("[Redis OpenCV]   - {}", highlightPattern);

            return container;

        } catch (Exception e) {
            log.error("[Redis OpenCV] ✗ Failed to create message listener container", e);
            log.error("[Redis OpenCV] Error details: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize Redis message listener", e);
        }
    }
}