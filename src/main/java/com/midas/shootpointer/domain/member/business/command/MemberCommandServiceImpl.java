package com.midas.shootpointer.domain.member.business.command;

import com.midas.shootpointer.domain.member.business.MemberManager;
import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;
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
	
	@Override
	public UUID deleteMember(UUID memberId) {
		Member currentMember = memberManager.findMemberById(memberId);
		
		return memberManager.deleteMember(memberId, currentMember);
	}
	
	private String extractCodeFromRequest(HttpServletRequest request) {
		return request.getParameter("code");
	}
}
