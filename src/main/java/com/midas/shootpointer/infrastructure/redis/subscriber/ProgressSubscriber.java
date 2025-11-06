package com.midas.shootpointer.infrastructure.redis.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.infrastructure.redis.dto.ProgressRedisResponse;
import com.midas.shootpointer.infrastructure.redis.helper.ProgressValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor

/**
 * Redis SUB 메시지 수신 클래스
 */
public class ProgressSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final ProgressValidator validator;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String body=new String(message.getBody());
            log.info("[Redis sub] Received message : {}",body);

            ProgressRedisResponse progress=objectMapper.readValue(body,ProgressRedisResponse.class);

            //null값인 경우
            if (progress==null || progress.data()==null){
                log.error("[Redis sub] progress data is null : time = {}", LocalDateTime.now());
                return;
            }

            //SUB로 받은 값  null 검증
            validator.validate(progress.data());

            //TODO: SSE로 client에 전달 로직.

            log.info("[Redis sub] progress info : jobId = {}",progress.data().jobId());

        }catch (Exception e){
            log.error("[Redis sub] failed to process message : {}",e.getMessage());
        }
    }
}
