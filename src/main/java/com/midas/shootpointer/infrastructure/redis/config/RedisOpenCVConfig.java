package com.midas.shootpointer.infrastructure.redis.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.midas.shootpointer.infrastructure.openCV.OpenCVProperties;
import com.midas.shootpointer.infrastructure.redis.subscriber.ProgressSubscriber;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisOpenCVConfig {
    private final String host;
    private final int port;
    private final String highlightChannel;
    private final String uploadChannel;

    public RedisOpenCVConfig(
            OpenCVProperties openCVProperties
    ){
        this.host=openCVProperties.getRedis().getHost();
        this.port=openCVProperties.getRedis().getPort();
        this.highlightChannel=openCVProperties.getRedis().getChannels().getHighlight();
        this.uploadChannel=openCVProperties.getRedis().getChannels().getUpload();
    }

    /**
     * 채널 빈 생성
     */
    @Bean
    public ChannelTopic uploadChannelTopic(){
        return new ChannelTopic(uploadChannel);
    }

    @Bean
    public ChannelTopic highlightChannelTopic(){
        return new ChannelTopic(highlightChannel);
    }

    @Bean(name = "opencvRedisConnectionFactory")
    public RedisConnectionFactory opencvRedisConnectionFactory(){
        RedisStandaloneConfiguration configuration=new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);

        return new LettuceConnectionFactory(configuration);
    }

    @Bean(name = "opencvRedisTemplate")
    public RedisTemplate<String,Object> opencvRedisTemplate(
            @Qualifier("opencvRedisConnectionFactory") RedisConnectionFactory connectionFactory
    ){
        RedisTemplate<String,Object> template=new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper objectMapper = new ObjectMapper()
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                .registerModule(new JavaTimeModule());

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        return template;
    }

    //Redis 메시지 수진하고 리스너에 전달 컨테이너
    @Bean
    public RedisMessageListenerContainer redisMessage(
            @Qualifier("opencvRedisConnectionFactory") RedisConnectionFactory connectionFactory,
            ProgressSubscriber progressSubscriber
    ){
        /**
         * 1. Redis 연결 컨테이너 생성
         */
        RedisMessageListenerContainer container=new RedisMessageListenerContainer();

        /**
         * 2. 연결 설정 주입
         */
        container.setConnectionFactory(connectionFactory);

        /**
         * 3. 채널로 구독자 등록
         */
        //원본 영상 업로드 채널 구독
        container.addMessageListener(progressSubscriber,uploadChannelTopic());
        //하이라이트 영상 채널 구독
        container.addMessageListener(progressSubscriber,highlightChannelTopic());

        return container;
    }
}
