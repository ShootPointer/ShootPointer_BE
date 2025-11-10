package com.midas.shootpointer.domain.member.business.query;

import com.midas.shootpointer.domain.member.business.MemberManager;
import com.midas.shootpointer.domain.member.dto.MemberResponseDto;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.helper.MemberHelper;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService {
	
	private final MemberHelper memberHelper;
	private final MemberManager memberManager;

	@Override
	public Optional<Member> findByEmail(String email) {
		try {
			Member member = memberHelper.findMemberByEmail(email);
			return Optional.of(member);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	@Override
	public MemberResponseDto getMemberInfo(UUID memberId) {
		return memberManager.getMemberInfo(memberId);
	}

}
