package com.midas.shootpointer.domain.member.business.query;

import com.midas.shootpointer.domain.member.entity.Member;
import java.util.Optional;

public interface MemberQueryService {
	
	Optional<Member> findByEmail(String email);
	Optional<Member> findByEncryptEmail(String email);
}
