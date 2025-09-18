package com.midas.shootpointer.global.security;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
class SecurityUtilsTest {
	
	private Member member;
	private CustomUserDetails customUserDetails;
	
	@BeforeEach
	void setUp() {
		SecurityContextHolder.clearContext();
		
		member = Member.builder()
			.memberId(UUID.randomUUID())
			.email("test123@naver.com")
			.username("test123")
			.build();
		
		customUserDetails = new CustomUserDetails(member);
	}
	
	@Test
	@DisplayName("인증된 사용자 Member 객체 반환_SUCCESS")
	void getCurrentMember_AuthenticatedUser_ReturnsMember() {
		// given
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			customUserDetails, null, customUserDetails.getAuthorities()
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		// when
		Member result = SecurityUtils.getCurrentMember();
		// then
		assertThat(result).isNotNull();
		assertThat(result.getMemberId()).isEqualTo(member.getMemberId());
		assertThat(result.getEmail()).isEqualTo("test123@naver.com");
		assertThat(result.getUsername()).isEqualTo("test123");
	}
	
	@Test
	@DisplayName("인증되지 않은 사용자일 때 예외 발생_FAILED")
	void getCurrentMember_NotAuthenticated_ThrowsException() {
		// given
		SecurityContextHolder.clearContext();
		// when & then
		assertThatThrownBy(() -> SecurityUtils.getCurrentMember())
			.isInstanceOf(CustomException.class)
			.hasMessage(ErrorCode.UNAUTHORIZED_MEMBER_ACCESS.getMessage());
	}
	
	@Test
	@DisplayName("getCurrentMemberId - 인증된 사용자 ID 반환_SUCCESS")
	void getCurrentMemberId_Authenticated_User_ReturnsMemberId() {
		// given
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			customUserDetails, null, customUserDetails.getAuthorities()
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		// when
		UUID result = SecurityUtils.getCurrentMemberId();
		// then
		assertThat(result).isEqualTo(member.getMemberId());
	}
	
}