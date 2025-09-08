package com.midas.shootpointer.domain.member.business.query;

import com.midas.shootpointer.domain.member.dto.MemberResponseDto;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.helper.MemberHelper;
import com.midas.shootpointer.domain.member.mapper.MemberMapper;
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
	private final MemberMapper memberMapper;
	
	@Override
	public Optional<MemberResponseDto> findByEmail(String email) {
		try {
			Member member = memberHelper.findMemberByEmail(email);
			return Optional.of(memberMapper.entityToDto(member));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

}
