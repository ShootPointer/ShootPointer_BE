package com.midas.shootpointer.domain.member.helper;

import com.midas.shootpointer.domain.highlight.repository.HighlightQueryRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberCommandRepository;
import com.midas.shootpointer.domain.member.repository.MemberQueryRepository;
import com.midas.shootpointer.domain.memberbacknumber.repository.MemberBackNumberRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberUtilImpl implements MemberUtil {
	
	private final MemberQueryRepository memberQueryRepository;
	private final MemberCommandRepository memberCommandRepository;
	private final MemberBackNumberRepository memberBackNumberRepository;
	private final HighlightQueryRepository highlightQueryRepository;
	
	@Override
	public Member findMemberById(UUID memberId) {
		return memberQueryRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
	}
	
	@Override
	public Member findMemberByEmail(String email) {
		return memberQueryRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
	}
	
	@Override
	public Member save(Member member) {
		return memberCommandRepository.save(member);
	}
	
	@Override
	public void delete(Member member) {
		memberCommandRepository.delete(member);
	}
	
	@Override
	public boolean existsByEmail(String email) {
		return memberQueryRepository.findByEmail(email).isPresent();
	}
	
	@Override
	public Integer getBackNumber(UUID memberId) {
		return memberBackNumberRepository.findByMemberId(memberId)
			.map(memberBackNumber -> memberBackNumber.getBackNumber().getBackNumber().getNumber())
			.orElse(0);
	}
	
	@Override
	public Integer getTotalTwoPointCount(UUID memberId) {
		return highlightQueryRepository.sumTwoPointCountByMemberId(memberId);
	}
	
	@Override
	public Integer getTotalThreePointCount(UUID memberId) {
		return highlightQueryRepository.sumThreePointCountByMemberId(memberId);
	}
	
	@Override
	public Integer getHighlightCount(UUID memberId) {
		return highlightQueryRepository.countByMemberId(memberId);
	}
}
