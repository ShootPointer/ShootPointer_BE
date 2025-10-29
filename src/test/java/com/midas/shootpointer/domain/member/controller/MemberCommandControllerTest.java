package com.midas.shootpointer.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midas.shootpointer.BaseSpringBootTest;
import com.midas.shootpointer.WithMockCustomMember;
import com.midas.shootpointer.domain.member.business.command.MemberCommandService;
import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.security.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("MemberCommandController 통합 테스트")
class MemberCommandControllerIntegrationTest extends BaseSpringBootTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockitoBean
	private MemberCommandService memberCommandService;
	@WithMockCustomMember
	@Test
	@DisplayName("카카오 로그인 콜백 - 성공")
	void callback_Success() throws Exception {
		// given
		String accessToken = "test_access_token";
		String refreshToken = "test_refresh_token";
		
		KakaoDTO kakaoDTO = KakaoDTO.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
		
		when(memberCommandService.processKakaoLogin(any())).thenReturn(kakaoDTO);
		
		// when & then
		mockMvc.perform(get("/kakao/callback")
				.param("code", "test_authorization_code"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("Success"))
			.andExpect(jsonPath("$.result.accessToken").value(accessToken))
			.andExpect(jsonPath("$.result.refreshToken").value(refreshToken));
		
		verify(memberCommandService, times(1)).processKakaoLogin(any());
	}
	@WithMockCustomMember
	@Test
	@DisplayName("회원 탈퇴 - 성공")
	void deleteMember_Success() throws Exception {
		// given
		UUID memberId = UUID.randomUUID();
		Member member = createMember(memberId, "test@example.com", "testUser");
		CustomUserDetails userDetails = new CustomUserDetails(member);
		
		when(memberCommandService.deleteMember(any(Member.class))).thenReturn(memberId);
		
		// when & then
		mockMvc.perform(delete("/kakao")
				.with(user(userDetails)))
			.andDo(print())
			.andExpect(status().isOk());
		
		verify(memberCommandService, times(1)).deleteMember(any(Member.class));
	}


	@Test
	@DisplayName("회원 탈퇴 - 인증되지 않은 사용자")
	void deleteMember_Unauthorized() throws Exception {
		// when & then
		mockMvc.perform(delete("/kakao"))
			.andDo(print())
			.andExpect(status().isUnauthorized());
		
		verify(memberCommandService, never()).deleteMember(any(Member.class));
	}
	@WithMockCustomMember
	@Test
	@DisplayName("회원 정보 조회 - 성공")
	void getCurrentMember_Success() throws Exception {
		// given
		UUID memberId = UUID.randomUUID();
		String email = "test@example.com";
		String username = "testUser";
		
		Member member = createMember(memberId, email, username);
		CustomUserDetails userDetails = new CustomUserDetails(member);
		
		// when & then
		mockMvc.perform(get("/kakao/me")
				.with(user(userDetails)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.memberId").value(memberId.toString()))
			.andExpect(jsonPath("$.email").value(email))
			.andExpect(jsonPath("$.username").value(username));
	}

	@Test
	@DisplayName("회원 정보 조회 - 인증되지 않은 사용자")
	void getCurrentMember_Unauthorized() throws Exception {
		// when & then
		mockMvc.perform(get("/kakao/me"))
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}
	private Member createMember(UUID memberId, String email, String username) {
		return Member.builder()
			.memberId(memberId)
			.email(email)
			.username(username)
			.build();
	}
}