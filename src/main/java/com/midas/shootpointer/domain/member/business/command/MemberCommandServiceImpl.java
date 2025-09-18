package com.midas.shootpointer.domain.member.business.command;

import com.midas.shootpointer.domain.member.business.MemberManager;
import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.util.jwt.handler.RefreshTokenHandler;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService {
	
	private final MemberManager memberManager;
	private final KakaoService kakaoService;
	private final TokenService tokenService;
	private final RefreshTokenHandler refreshTokenHandler;
	
	@Override
	public KakaoDTO processKakaoLogin(HttpServletRequest request) {
		memberManager.validateKakaoCallback(request);
		String code = extractCodeFromRequest(request);
		
		KakaoDTO kakaoInfo = kakaoService.getKakaoUserInfo(code);
		Member member = memberManager.processKakaoLogin(request, kakaoInfo);
		
		// JWT 발급 및 DTO에 토큰 세팅
		tokenService.generateTokens(member, kakaoInfo);
		
		return kakaoInfo;
	}

	private String extractCodeFromRequest(HttpServletRequest request) {
		return request.getParameter("code");
	}
	
	@Override
	public UUID deleteMember(Member member) {
		// DB에서 회원 삭제
		UUID deletedMemberId = memberManager.deleteMember(member.getMemberId(), member);
		// Redis에서 회원 탈퇴 시 Refresh Token 삭제
		refreshTokenHandler.deleteRefreshToken(member.getEmail());
		
		return deletedMemberId;
	}
}
