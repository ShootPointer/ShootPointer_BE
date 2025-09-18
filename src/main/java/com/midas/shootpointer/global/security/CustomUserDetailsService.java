package com.midas.shootpointer.global.security;

import com.midas.shootpointer.domain.member.business.query.MemberQueryService;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {
	
	private final MemberQueryService memberQueryService;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Member member = memberQueryService.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
		
		return new CustomUserDetails(member);
	}
}
