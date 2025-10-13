package com.midas.shootpointer.domain.member.business.command;

import com.midas.shootpointer.domain.member.business.MemberManager;
import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.util.jwt.handler.RefreshTokenHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberCommandService 테스트")
class MemberCommandServiceImplTest {
	
	@Mock
	private MemberManager memberManager;
	
	@Mock
	private KakaoService kakaoService;
	
	@Mock
	private TokenService tokenService;
	
	@Mock
	private RefreshTokenHandler refreshTokenHandler;
	
	@Mock
	private HttpServletRequest request;
	
	@InjectMocks
	private MemberCommandServiceImpl memberCommandService;
	
	private Member testMember;
	private KakaoDTO testKakaoDTO;
	private String testCode;
	
	@BeforeEach
	void setUp() {
		testCode = "test_code_123";
		testMember = Member.builder()
			.memberId(UUID.randomUUID())
			.username("testUser")
			.email("test@kakao.com")
			.build();
		
		testKakaoDTO = KakaoDTO.builder()
			.email("test@kakao.com")
			.nickname("testUser")
			.build();
	}
	
	@Test
	@DisplayName("카카오 로그인 처리 및 JWT 발급")
	void testProcessKakaoLogin() {
		// given
		doNothing().when(memberManager).validateKakaoCallback(request);
		when(request.getParameter("code")).thenReturn(testCode);
		when(kakaoService.getKakaoUserInfo(testCode)).thenReturn(testKakaoDTO);
		when(memberManager.processKakaoLogin(request, testKakaoDTO)).thenReturn(testMember);
		doNothing().when(tokenService).generateTokens(testMember, testKakaoDTO);
		
		// when
		KakaoDTO result = memberCommandService.processKakaoLogin(request);
		
		// then
		assertNotNull(result);
		assertEquals(testKakaoDTO.getEmail(), result.getEmail());
		verify(memberManager, times(1)).validateKakaoCallback(request);
		verify(kakaoService, times(1)).getKakaoUserInfo(testCode);
		verify(tokenService, times(1)).generateTokens(testMember, testKakaoDTO);
	}
	
	@Test
	@DisplayName("회원 탈퇴 - 데이터베이스 삭제 및 Redis Refresh Token 삭제")
	void testDeleteMember() {
		// given
		UUID memberId = testMember.getMemberId();
		when(memberManager.deleteMember(memberId, testMember)).thenReturn(memberId);
		doNothing().when(refreshTokenHandler).deleteRefreshToken(testMember.getEmail());
		
		// when
		UUID result = memberCommandService.deleteMember(testMember);
		
		// then
		assertNotNull(result);
		assertEquals(memberId, result);
		verify(memberManager, times(1)).deleteMember(memberId, testMember);
		verify(refreshTokenHandler, times(1)).deleteRefreshToken(testMember.getEmail());
	}

	@Test
	@DisplayName("회원의 하이라이트 영상 수집 상태를 true로 변환합니다. - MemberManager-agree메서드가 동작하는지 검증합니다.")
	void agree(){
		//given

		//when

		//then

	}
}