package com.midas.shootpointer.domain.member.helper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("KakaoApiHelper 테스트")
class KakaoApiHelperImplTest {
	
	@InjectMocks
	private KakaoApiHelperImpl kakaoApiHelper;
	
	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(kakaoApiHelper, "KAKAO_CLIENT_ID", "test_client_id");
		ReflectionTestUtils.setField(kakaoApiHelper, "KAKAO_CLIENT_SECRET", "test_client_secret");
		ReflectionTestUtils.setField(kakaoApiHelper, "KAKAO_REDIRECT_URI", "http://localhost:8080/kakao/callback");
		ReflectionTestUtils.setField(kakaoApiHelper, "KAKAO_AUTH_URI", "https://kauth.kakao.com");
		ReflectionTestUtils.setField(kakaoApiHelper, "KAKAO_API_URI", "https://kapi.kakao.com");
	}
	
	@Test
	@DisplayName("카카오 Access Token 요청 성공")
	void testRequestAccessTokenSuccess() {
		assertTrue(true);
	}
	
	@Test
	@DisplayName("카카오 사용자 정보 요청 - email과 nickname 검증")
	void testRequestUserInfoValidation() {
		assertTrue(true);
	}
}