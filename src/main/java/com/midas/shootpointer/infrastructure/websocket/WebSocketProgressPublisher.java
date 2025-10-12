package com.midas.shootpointer.infrastructure.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.util.UUID;

/**
 * 웹 소켓 전송(단 방향) 관리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketProgressPublisher {
    private final WebSocketSessionManager sessionManager;
    private final ObjectMapper mapper;


    public void publish(UUID memberId,ProgressMsg msg){
        try {
            String json=mapper.writeValueAsString(msg);
            sessionManager.sendMessage(memberId,new TextMessage(json));
        }catch (JsonProcessingException e){
            log.error("json 변환 실패 {}",e.getMessage());
            throw new CustomException(ErrorCode.FAILED_PARSING_JSON);
        }

    }
}
