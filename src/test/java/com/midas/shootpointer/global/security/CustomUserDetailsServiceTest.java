package com.midas.shootpointer.global.security;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.midas.shootpointer.domain.member.business.query.MemberQueryService;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.exception.CustomException;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
	
	@Mock
	private MemberQueryService memberQueryService;
	
	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;
	
	@Test
	@DisplayName("존재하는 이메일로 사용자 조회_SUCCESS")
	void loadUserByUsername_ExistEmail_ReturnsCustomUserDetails() {
		// given
		String email = "test123@naver.com";
		Member member = Member.builder()
			.memberId(UUID.randomUUID())
			.email(email)
			.username("test123")
			.build();
		when(memberQueryService.findByEmail(email)).thenReturn(Optional.of(member));
		// when
		UserDetails result = customUserDetailsService.loadUserByUsername(email);
		// then
		assertThat(result).isInstanceOf(CustomUserDetails.class);
		CustomUserDetails customUserDetails = (CustomUserDetails) result;
		assertThat(customUserDetails.getMember()).isEqualTo(member);
		assertThat(customUserDetails.getUsername()).isEqualTo("test123");
		assertThat(customUserDetails.getMember().getEmail()).isEqualTo(email);
	}
	
	@Test
	@DisplayName("존재하지 않는 이메일로 사용자 조회 시 예외 발생_FAILED")
	void loadUserByUsername_NotExistEmail_ThrowsCustomException() {
		// given
		String email = "test123@naver.com";
		when(memberQueryService.findByEmail(anyString())).thenReturn(Optional.empty());
		
		// when & then
		assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(email))
			.isInstanceOf(CustomException.class)
			.hasMessage("Member를 찾을 수 없음."); // MEMBER_NOT_FOUND
	}
}