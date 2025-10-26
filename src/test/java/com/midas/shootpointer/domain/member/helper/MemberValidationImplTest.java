package com.midas.shootpointer.domain.member.helper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberValidation 테스트")
class MemberValidationImplTest {
	
	@Mock
	private HttpServletRequest request;
	
	@InjectMocks
	private MemberValidationImpl memberValidation;
	
	private KakaoDTO validKakaoDTO;
	
	@BeforeEach
	void setUp() {
		validKakaoDTO = KakaoDTO.builder()
			.email("test@kakao.com")
			.nickname("testUser")
			.build();
	}
	
	@Test
	@DisplayName("유효한 KakaoDTO 검증")
	void testValidateKakaoDTO_Valid() {
		// when & then
		assertDoesNotThrow(() -> memberValidation.validateKakaoDTO(validKakaoDTO));
	}
	
	@Test
	@DisplayName("KakaoDTO null 검증 실패")
	void testValidateKakaoDTO_Null() {
		// when & then
		assertThrows(CustomException.class, () -> memberValidation.validateKakaoDTO(null));
	}
	
	@Test
	@DisplayName("빈 이메일 검증 실패")
	void testValidateKakaoDTO_EmptyEmail() {
		// given
		KakaoDTO invalidDTO = KakaoDTO.builder()
			.email("")
			.nickname("testUser")
			.build();
		
		// when & then
		assertThrows(CustomException.class, () -> memberValidation.validateKakaoDTO(invalidDTO));
	}
	
	@Test
	@DisplayName("잘못된 이메일 형식 검증 실패")
	void testValidateKakaoDTO_InvalidEmailFormat() {
		// given
		KakaoDTO invalidDTO = KakaoDTO.builder()
			.email("invalid-email")
			.nickname("testUser")
			.build();
		
		// when & then
		assertThrows(CustomException.class, () -> memberValidation.validateKakaoDTO(invalidDTO));
	}
	
	@Test
	@DisplayName("유효한 카카오 콜백 검증")
	void testValidateKakaoCallback_Valid() {
		// given
		when(request.getParameter("error")).thenReturn(null);
		when(request.getParameter("code")).thenReturn("valid_code");
		
		// when & then
		assertDoesNotThrow(() -> memberValidation.validateKakaoCallback(request));
	}
	
	@Test
	@DisplayName("카카오 콜백 에러 검증 실패")
	void testValidateKakaoCallback_WithError() {
		// given
		when(request.getParameter("error")).thenReturn("access_denied");
		
		// when & then
		assertThrows(CustomException.class, () -> memberValidation.validateKakaoCallback(request));
	}
	
	@Test
	@DisplayName("회원 소유권 검증 - 성공")
	void testIsMemberOwner_Success() {
		// given
		UUID memberId = UUID.randomUUID();
		Member member = Member.builder()
			.memberId(memberId)
			.username("testUser")
			.email("test@kakao.com")
			.build();
		
		// when & then
		assertDoesNotThrow(() -> memberValidation.isMemberOwner(member, memberId));
	}
	
	@Test
	@DisplayName("회원 소유권 검증 - 실패")
	void testIsMemberOwner_Failure() {
		// given
		UUID memberId = UUID.randomUUID();
		UUID differentId = UUID.randomUUID();
		Member member = Member.builder()
			.memberId(memberId)
			.username("testUser")
			.email("test@kakao.com")
			.build();
		
		// when & then
		assertThrows(CustomException.class,
			() -> memberValidation.isMemberOwner(member, differentId));
	}
}