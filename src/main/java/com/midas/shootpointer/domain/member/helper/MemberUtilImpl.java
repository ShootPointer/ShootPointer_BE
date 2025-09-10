package com.midas.shootpointer.domain.member.helper;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberCommandRepository;
import com.midas.shootpointer.domain.member.repository.MemberQueryRepository;
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
}
