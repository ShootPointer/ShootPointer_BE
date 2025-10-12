package com.midas.shootpointer.domain.member.business.command;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.util.jwt.handler.JwtHandler;
import com.midas.shootpointer.global.util.jwt.handler.RefreshTokenHandler;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenService 테스트")
class TokenServiceImplTest {
	
	@Mock
	private JwtHandler jwtHandler;
	
	@Mock
	private RefreshTokenHandler refreshTokenHandler;
	
	@InjectMocks
	private TokenServiceImpl tokenService;
	
	private Member testMember;
	private KakaoDTO testKakaoDTO;
	private String testAccessToken;
	private String testRefreshToken;
	
	@BeforeEach
	void setUp() {
		testMember = Member.builder()
			.memberId(UUID.randomUUID())
			.username("testUser")
			.email("test@kakao.com")
			.build();
		
		testKakaoDTO = KakaoDTO.builder()
			.email("test@kakao.com")
			.nickname("testUser")
			.build();
		
		testAccessToken = "test_access_token";
		testRefreshToken = "test_refresh_token";
	}
	
	@Test
	@DisplayName("새로운 Access Token과 Refresh Token 생성")
	void testGenerateTokensNewTokens() {
		// given
		when(jwtHandler.createAccessToken(testMember)).thenReturn(testAccessToken);
		when(refreshTokenHandler.getRefreshToken(testMember.getEmail())).thenReturn(Optional.empty());
		when(jwtHandler.createRefreshToken(testMember.getEmail())).thenReturn(testRefreshToken);
		
		// when
		tokenService.generateTokens(testMember, testKakaoDTO);
		
		// then
		assertEquals(testAccessToken, testKakaoDTO.getAccessToken());
		assertEquals(testRefreshToken, testKakaoDTO.getRefreshToken());
		verify(jwtHandler, times(1)).createAccessToken(testMember);
		verify(jwtHandler, times(1)).createRefreshToken(testMember.getEmail());
		verify(refreshTokenHandler, times(1)).saveRefreshToken(testMember.getEmail(), testRefreshToken);
	}
	
	@Test
	@DisplayName("기존 유효한 Refresh Token 재사용")
	void testGenerateTokensReuseExistingToken() {
		// given
		String existingRefreshToken = "existing_refresh_token";
		when(jwtHandler.createAccessToken(testMember)).thenReturn(testAccessToken);
		when(refreshTokenHandler.getRefreshToken(testMember.getEmail()))
			.thenReturn(Optional.of(existingRefreshToken));
		when(jwtHandler.validateToken(existingRefreshToken)).thenReturn(true);
		
		// when
		tokenService.generateTokens(testMember, testKakaoDTO);
		
		// then
		assertEquals(testAccessToken, testKakaoDTO.getAccessToken());
		assertEquals(existingRefreshToken, testKakaoDTO.getRefreshToken());
		verify(jwtHandler, never()).createRefreshToken(any());
		verify(refreshTokenHandler, never()).saveRefreshToken(any(), any());
	}
	
	@Test
	@DisplayName("만료된 Refresh Token 갱신")
	void testGenerateTokensRefreshExpiredToken() {
		// given
		String expiredRefreshToken = "expired_refresh_token";
		when(jwtHandler.createAccessToken(testMember)).thenReturn(testAccessToken);
		when(refreshTokenHandler.getRefreshToken(testMember.getEmail()))
			.thenReturn(Optional.of(expiredRefreshToken));
		when(jwtHandler.validateToken(expiredRefreshToken)).thenReturn(false);
		when(jwtHandler.createRefreshToken(testMember.getEmail())).thenReturn(testRefreshToken);
		
		// when
		tokenService.generateTokens(testMember, testKakaoDTO);
		
		// then
		assertEquals(testAccessToken, testKakaoDTO.getAccessToken());
		assertEquals(testRefreshToken, testKakaoDTO.getRefreshToken());
		verify(jwtHandler, times(1)).createRefreshToken(testMember.getEmail());
		verify(refreshTokenHandler, times(1)).saveRefreshToken(testMember.getEmail(), testRefreshToken);
	}
}