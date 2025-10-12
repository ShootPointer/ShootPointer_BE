package com.midas.shootpointer.global.config;

import com.midas.shootpointer.infrastructure.websocket.HandshakeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketHandler webSocketHandler;
    private final HandshakeHandler handshakeHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
            registry
                    .addHandler(webSocketHandler,"/ws/progress")
                    .addInterceptors(handshakeHandler)
                    .setAllowedOriginPatterns("*");
    }
}
