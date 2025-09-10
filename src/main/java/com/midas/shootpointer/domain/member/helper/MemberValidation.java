package com.midas.shootpointer.domain.member.helper;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

public interface MemberValidation {
	
	void validateKakaoDTO(KakaoDTO kakaoDTO);
	
	void validateKakaoCallback(HttpServletRequest request);
	
	void validateKakaoCode(String code);
	
	void isMemberExists(String email);
	
	void isMemberOwner(Member member, UUID memberId);
	
}
