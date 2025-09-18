package com.midas.shootpointer.global.util.jwt.handler;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenHandlerImpl implements RefreshTokenHandler {
	
	private final RedisTemplate<String, String> redisTemplate;
	
	@Value("${jwt.refresh_expiration_time}")
	private long refreshExpireMs;
	
	@Override
	public void saveRefreshToken(String email, String refreshToken) {
		redisTemplate.opsForValue()
			.set(buildKey(email), refreshToken, Duration.ofMillis(refreshExpireMs));
	}
	
	@Override
	public Optional<String> getRefreshToken(String email) {
		return Optional.ofNullable(redisTemplate.opsForValue().get(buildKey(email)));
	}
	
	@Override
	public void deleteRefreshToken(String email) {
		redisTemplate.delete(buildKey(email));
	}
	
	private String buildKey(String email) {
		return "refresh:" + email.toLowerCase();
	}
}
