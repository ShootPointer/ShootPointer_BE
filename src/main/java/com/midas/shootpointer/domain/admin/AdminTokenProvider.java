package com.midas.shootpointer.domain.admin;

import com.midas.shootpointer.global.util.jwt.JwtUtil;
import com.midas.shootpointer.global.util.jwt.handler.RefreshTokenHandlerImpl;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdminTokenProvider {
    
    private final JwtUtil jwtUtil;
    private final RefreshTokenHandlerImpl refreshTokenHandler;
    
    // AccessToken 생성
    public String createAccessToken(UUID adminId, String email, String nickname) {
        return jwtUtil.createToken(adminId, email, nickname);
    }
    
    // RefreshToken 생성 및 Redis 저장
    public String createAndSaveRefreshToken(String email) {
        String refreshToken = jwtUtil.createRefreshToken(email);
        refreshTokenHandler.saveRefreshToken(email, refreshToken);
        return refreshToken;
    }
    
    // Redis에서 RefreshToken 조회
    public Optional<String> getRefreshToken(String email) {
        return refreshTokenHandler.getRefreshToken(email); // Optional<String> 그대로 반환
    }

}
