package com.midas.shootpointer.domain.member.business.command;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.util.jwt.handler.JwtHandler;
import com.midas.shootpointer.global.util.jwt.handler.RefreshTokenHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
	
	private final JwtHandler jwtHandler;
	private final RefreshTokenHandler refreshTokenHandler;
	
	@Override
	public void generateTokens(Member member, KakaoDTO kakaoDTO) {
		// Access Token 생성
		String accessToken = jwtHandler.createAccessToken(member);
		
		// Refresh Token 생성
		String refreshToken = jwtHandler.createRefreshToken(member.getEmail());
		
		// Refresh Token Redis에 저장
		refreshTokenHandler.saveRefreshToken(member.getEmail(), refreshToken);
		
		// DTO에 토큰 정보 설정
		kakaoDTO.setAccessToken(accessToken);
		kakaoDTO.setRefreshToken(refreshToken);
	}
}
