package com.midas.shootpointer.domain.member.business.query;

import com.midas.shootpointer.domain.member.dto.MemberResponseDto;
import java.util.Optional;

public interface MemberQueryService {
	
	Optional<MemberResponseDto> findByEmail(String email);
	
}
