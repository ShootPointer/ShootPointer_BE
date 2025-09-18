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
		
		// Refresh Token 생성 -> 기존에 존재하면 해당 Refresh Token 설정 / 만료 되었다면 새로운 Refresh Token 생성
		String existingRefreshToken = refreshTokenHandler.getRefreshToken(member.getEmail())
			.orElse(null);
		String refreshToken;
		
		if (existingRefreshToken != null && jwtHandler.validateToken(existingRefreshToken)) {
			refreshToken = existingRefreshToken;
		} else {
			refreshToken = jwtHandler.createRefreshToken(member.getEmail());
			refreshTokenHandler.saveRefreshToken(member.getEmail(), refreshToken);
		}
		
		// DTO에 토큰 정보 설정
		kakaoDTO.setAccessToken(accessToken);
		kakaoDTO.setRefreshToken(refreshToken);
	}
}
