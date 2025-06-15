package com.midas.shootpointer.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.refresh_expiration_time}")
    private long refreshExpireMs;

    /**
     * 리프레시 토큰 생성, 조회, 삭제
     */

    public void save(String email, String refreshToken) {
        redisTemplate.opsForValue()
                .set(buildKey(email), refreshToken, Duration.ofMillis(refreshExpireMs));
    }

    public String get(String email) {
        return redisTemplate.opsForValue().get(buildKey(email));
    }

    public void delete(String email) {
        redisTemplate.delete(buildKey(email));
    }

    private String buildKey(String email) {
        return "refresh : " + email;
    }

}
