package com.midas.shootpointer.infrastructure.websocket;

import com.midas.shootpointer.global.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandshakeHandler implements HandshakeInterceptor {
    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        /**
         * 1. JWT 추출
         */
        URI uri=request.getURI();
        String query=uri.getQuery();

        log.info("요청 받은 쿼리 : {}",query);

        if (query!=null && query.startsWith("token=")){
            String token=query.substring("token=".length());
            UUID memberId= jwtUtil.getMemberId(token);
            if(memberId!=null){
                attributes.put("memberId",memberId);
                return true;
            }
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        //do Nothing
    }
}
