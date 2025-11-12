package com.midas.shootpointer.domain.member.helper;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberHelperImpl implements MemberHelper {
	
	private final MemberValidation memberValidation;
	private final MemberUtil memberUtil;
	
	@Override
	public void validateKakaoDTO(KakaoDTO kakaoDTO) {
		memberValidation.validateKakaoDTO(kakaoDTO);
	}
	
	@Override
	public void validateKakaoCallback(HttpServletRequest request) {
		memberValidation.validateKakaoCallback(request);
	}
	
	@Override
	public void validateKakaoCode(String code) {
		memberValidation.validateKakaoCode(code);
	}
	
	@Override
	public void isMemberExists(String email) {
		memberValidation.isMemberExists(email);
	}
	
	@Override
	public void isMemberOwner(Member member, UUID memberId) {
		memberValidation.isMemberOwner(member, memberId);
	}
	
	@Override
	public Member findMemberById(UUID memberId) {
		return memberUtil.findMemberById(memberId);
	}
	
	@Override
	public Member findMemberByEmail(String email) {
		return memberUtil.findMemberByEmail(email);
	}
	
	@Override
	public Member save(Member member) {
		return memberUtil.save(member);
	}
	
	@Override
	public void delete(Member member) {
		memberUtil.delete(member);
	}
	
	@Override
	public boolean existsByEmail(String email) {
		return memberUtil.existsByEmail(email);
	}
	
	@Override
	public Integer getBackNumber(UUID memberId) {
		return memberUtil.getBackNumber(memberId);
	}
	
	@Override
	public Integer getTotalTwoPointCount(UUID memberId) {
		return memberUtil.getTotalTwoPointCount(memberId);
	}
	
	@Override
	public Integer getTotalThreePointCount(UUID memberId) {
		return memberUtil.getTotalThreePointCount(memberId);
	}
	
	@Override
	public Integer getHighlightCount(UUID memberId) {
		return memberUtil.getHighlightCount(memberId);
	}
	
}
