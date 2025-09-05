package com.midas.shootpointer.global.util.jwt.handler;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
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
			jwtUtil.parseToken(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
