package com.midas.shootpointer.infrastructure.redis.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.domain.progress.dto.ProgressRedisResponse;
import com.midas.shootpointer.domain.progress.mapper.ProgressMapper;
import com.midas.shootpointer.domain.progress.service.ProgressSseEmitter;
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
    private final ProgressSseEmitter emitter;
    private final ProgressMapper mapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String body=new String(message.getBody());
            log.info("[Redis SUB] Received message : {}",body);

            ProgressRedisResponse progress=objectMapper.readValue(body,ProgressRedisResponse.class);

            //null값인 경우
            if (progress==null || progress.data()==null){
                log.error("[Redis SUB] progress data is null : time = {}", LocalDateTime.now());
                return;
            }

            /**
             * success = false 또는 status가 200이 아닌 경우
             * 1. 현재는 장애 대응 메뉴얼 존재 X => 페일오버 대응 필요.
             * 2. log debug 으로 기록
             */
            if (!progress.success() || progress.status() != 200){
                log.debug("[Redis SUB] progress data response error : time = {}",LocalDateTime.now());
            }

            //SUB로 받은 값  null 검증
            validator.validate(progress.data());

            //SSE로 client에 전달
            emitter.sendToClient(
                    progress.data().memberId(),
                    mapper.progressDataToResponse(progress.data())
            );

            log.info("[Redis SUB] progress info : jobId = {}",progress.data().jobId());

        }catch (Exception e){
            log.error("[Redis SUB] failed to process message : {}",e.getMessage());
        }
    }
}
