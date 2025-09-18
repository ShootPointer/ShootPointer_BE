package com.midas.shootpointer.global.util.jwt.handler;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtHandlerImpl implements JwtHandler {
	
	private final JwtUtil jwtUtil;
	
	@Override
	public String createAccessToken(Member member) {
		return jwtUtil.createToken(member.getMemberId(), member.getEmail(), member.getUsername());
	}
	
	@Override
	public String createRefreshToken(String email) {
		return jwtUtil.createRefreshToken(email);
	}
	
	@Override
	public String getEmailFromToken(String token) {
		return jwtUtil.getEmailFromToken(token);
	}
	
	@Override
	public String getNicknameFromToken(String token) {
		return jwtUtil.getNicknameFromToken(token);
	}
	
	@Override
	public UUID getMemberIdFromToken(String token) {
		return jwtUtil.getMemberId(token);
	}
	
	@Override
	public boolean validateToken(String token) {
		try {
			Claims claims = jwtUtil.parseToken(token);
			Date expiration = claims.getExpiration();
			return expiration.after(new Date());
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 토큰 만료시간 반환
	 * @param token
	 * @return
	 */
	@Override
	public Date getTokenExpiration(String token) {
		try {
			Claims claims = jwtUtil.parseToken(token);
			return claims.getExpiration();
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 토큰이 만료되었는지 확인
	 * @param token
	 * @param threshHoldMs
	 * @return
	 */
	@Override
	public boolean isTokenExpired(String token, long threshHoldMs) {
		try {
			Claims claims = jwtUtil.parseToken(token);
			Date expiration = claims.getExpiration();
			Date now = new Date();
			
			return expiration.getTime() - now.getTime() < threshHoldMs;
		} catch (Exception e) {
			return true; // 예외 터지면 만료된거임
		}
	}
}
