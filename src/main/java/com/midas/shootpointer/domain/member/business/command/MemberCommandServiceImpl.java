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
	
	
	@Override
	public Member processKakaoLogin(HttpServletRequest request) {
		memberManager.validateKakaoCallback(request);
		String code = extractCodeFromRequest(request);
		
		KakaoDTO kakaoInfo = kakaoService.getKakaoUserInfo(code);
		
		return memberManager.processKakaoLogin(request, kakaoInfo);
	}
	
	@Override
	public UUID deleteMember(UUID memberId, Member currentMember) {
		return memberManager.deleteMember(memberId, currentMember);
	}
	
	private String extractCodeFromRequest(HttpServletRequest request) {
		return request.getParameter("code");
	}
}
