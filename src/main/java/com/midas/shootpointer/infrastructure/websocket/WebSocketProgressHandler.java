package com.midas.shootpointer.infrastructure.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketProgressHandler extends TextWebSocketHandler {
    private final WebSocketSessionManager sessionManager;
    private final WebSocketProgressPublisher progressPublisher;
    private final ObjectMapper mapper;

    /**
     * WebSocket 연결 성공 시 호출
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // JWT HandshakeInterceptor에서 memberId를 속성으로 넣음
        Object attr = session.getAttributes().get("memberId");

        if (attr == null) {
            log.warn("WebSocket 연결 불가 - memberId 없음");
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        UUID memberId = (UUID) attr;

        // 세션 매니저에 등록 (기존 세션이 있으면 교체됨)
        sessionManager.addSession(memberId, session);
        log.info("WebSocket 연결 성공: memberId={} sessionId={}", memberId, session.getId());

        //세션이 열려있는지 확인
        if(!session.isOpen()){
            log.warn("세션이 이미 닫힌 상태로 요청 time = {}", LocalDateTime.now());
            return;
        }
        try {
            // 연결 완료 메시지 클라이언트 전송
            ProgressMsg msg = ProgressMsg.of(PROGRESS_TYPE.UPLOAD_START, 0.0, "영상 업로드를 시작합니다.");
            String json=mapper.writeValueAsString(msg);

            //동시 전송 방지
            synchronized (session){
                session.sendMessage(new TextMessage(json));
            }
        }catch (IOException e){
            log.error("초기 메세지 전송 실패 {}",e.getMessage());
        }
    }


    /**
     * 연결 종료 시 세션 정리
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionManager.removeSession(session);
        log.info("WebSocket 연결 종료: sessionId={} reason={}", session.getId(), status.getReason());
    }

    /**
     * 예외 처리
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket 전송 오류: sessionId={} error={}", session.getId(), exception.getMessage());
        try {
            session.close(CloseStatus.SERVER_ERROR);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FAILED_END_WEB_SOCKET_FAILED);
        }
    }

}
