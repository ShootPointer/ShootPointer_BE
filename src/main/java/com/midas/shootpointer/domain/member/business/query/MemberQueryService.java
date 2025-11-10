package com.midas.shootpointer.domain.member.business.query;

import com.midas.shootpointer.domain.member.dto.MemberResponseDto;
import com.midas.shootpointer.domain.member.entity.Member;
import java.util.Optional;
import java.util.UUID;

public interface MemberQueryService {
	
	Optional<Member> findByEmail(String email);

	MemberResponseDto getMemberInfo(UUID memberId);
}
