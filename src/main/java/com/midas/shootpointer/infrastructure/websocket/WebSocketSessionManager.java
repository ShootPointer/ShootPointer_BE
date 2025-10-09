package com.midas.shootpointer.infrastructure.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketSessionManager {
    private final Map<UUID, WebSocketSession> sessions=new ConcurrentHashMap<>();
    private final ObjectMapper mapper=new ObjectMapper();

    /**
     * 웹소켓 세션 추가 메서드
     * @param memberId 웹 소켓 세션 키 값
     * @param session 새로 생성된 세션
     */
    public void addSession(UUID memberId, WebSocketSession session){
        /**
         *  이미 연결되 소켓 있으면 해제
         */
        WebSocketSession existing=sessions.put(memberId,session);
        if(existing!=null && existing.isOpen()){
            try {
                //세션 종료
                existing.close();
                log.info("기존 websocket 세션 종료 후 새 세션 교체 : memberId = {} time = {}",memberId, LocalDateTime.now());

            }catch (IOException e){
                throw new CustomException(ErrorCode.FAILED_END_WEB_SOCKET_FAILED);
            }
        }else {
            log.info("websocket 새 새션 등록 : memberId = {} time = {}",memberId,LocalDateTime.now());
        }
    }

    /**
     * 웹 소켓 세션 삭제 메서드
     * @param session 웹 소켓 세션
     */
    public void removeSession(WebSocketSession session){
        sessions.entrySet().removeIf(entry->
                entry.getValue().equals(session)
        );
        log.info("websocket 세션 제거 완료 time = {}",LocalDateTime.now());
    }

    /**
     * 웹 소켓 메시지 전송 메서드
     * @param memberId 웹 소켓 세션 키 값
     * @param message 전송할 메시지
     */
    public void sendMessage(UUID memberId, TextMessage message){
        WebSocketSession session=sessions.get(memberId);

        /**
         * 1. 세션이 없는 경우
         */
        if(session==null){
            log.warn("websocket 세션이 없음 : memberId = {} time = {}",memberId,LocalDateTime.now());
            return;
        }
        /**
         * 2. 세션이 닫혀 있는 경우
         */
        if (!session.isOpen()){
            log.warn("websocket 세션 닫힌 상태 : memberId = {} time = {}",memberId,LocalDateTime.now());
            sessions.remove(memberId);
            return;
        }
        /**
         * 3. 세션 메시지 전송
         */
        try {
            session.sendMessage(message);
        }catch (IOException e){
            log.error("websocket 세션 메세지 전송 실패 : memberId = {} time = {} error = {}",memberId,LocalDateTime.now(),e.getMessage());
            throw new CustomException(ErrorCode.FAILED_SEND_MESSAGE);
        }
    }

}
