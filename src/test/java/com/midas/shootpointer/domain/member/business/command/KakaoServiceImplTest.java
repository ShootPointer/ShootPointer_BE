package com.midas.shootpointer.domain.member.business.command;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.helper.KakaoApiHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("KakaoService 테스트")
class KakaoServiceImplTest {
	
	@Mock
	private KakaoApiHelper kakaoApiHelper;
	
	@InjectMocks
	private KakaoServiceImpl kakaoService;
	
	private String testCode;
	private String testAccessToken;
	private KakaoDTO testKakaoDTO;
	
	@BeforeEach
	void setUp() {
		testCode = "test_auth_code_12345";
		testAccessToken = "test_access_token_abcdef";
		testKakaoDTO = KakaoDTO.builder()
			.email("user@kakao.com")
			.nickname("testUser")
			.build();
	}
	
	@Test
	@DisplayName("카카오 인증 코드로 사용자 정보 조회")
	void testGetKakaoUserInfo() {
		// given
		when(kakaoApiHelper.requestAccessToken(testCode)).thenReturn(testAccessToken);
		when(kakaoApiHelper.requestUserInfo(testAccessToken)).thenReturn(testKakaoDTO);
		
		// when
		KakaoDTO result = kakaoService.getKakaoUserInfo(testCode);
		
		// then
		assertNotNull(result);
		assertEquals(testKakaoDTO.getEmail(), result.getEmail());
		assertEquals(testKakaoDTO.getNickname(), result.getNickname());
		verify(kakaoApiHelper, times(1)).requestAccessToken(testCode);
		verify(kakaoApiHelper, times(1)).requestUserInfo(testAccessToken);
	}
	
	@Test
	@DisplayName("카카오 Access Token 발급")
	void testGetKakaoAccessToken() {
		// given
		when(kakaoApiHelper.requestAccessToken(testCode)).thenReturn(testAccessToken);
		
		// when
		String result = kakaoService.getKakaoAccessToken(testCode);
		
		// then
		assertNotNull(result);
		assertEquals(testAccessToken, result);
		verify(kakaoApiHelper, times(1)).requestAccessToken(testCode);
	}
	
	@Test
	@DisplayName("Access Token으로 사용자 정보 요청")
	void testGetUserInfoWithToken() {
		// given
		when(kakaoApiHelper.requestUserInfo(testAccessToken)).thenReturn(testKakaoDTO);
		
		// when
		KakaoDTO result = kakaoService.getUserInfoWithToken(testAccessToken);
		
		// then
		assertNotNull(result);
		assertEquals(testKakaoDTO.getEmail(), result.getEmail());
		verify(kakaoApiHelper, times(1)).requestUserInfo(testAccessToken);
	}
}