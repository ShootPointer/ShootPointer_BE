package com.midas.shootpointer.domain.member.helper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;
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
@DisplayName("MemberHelper 테스트")
class MemberHelperImplTest {
	
	@Mock
	private MemberValidation memberValidation;
	
	@Mock
	private MemberUtil memberUtil;
	
	@Mock
	private HttpServletRequest request;
	
	@InjectMocks
	private MemberHelperImpl memberHelper;
	
	private Member testMember;
	private KakaoDTO testKakaoDTO;
	
	@BeforeEach
	void setUp() {
		testMember = Member.builder()
			.memberId(UUID.randomUUID())
			.username("testUser")
			.email("test@example.com")
			.build();
		
		testKakaoDTO = KakaoDTO.builder()
			.email("test@example.com")
			.nickname("testUser")
			.build();
	}
	
	@Test
	@DisplayName("KakaoDTO 유효성 검증 위임")
	void testValidateKakaoDTO() {
		// given
		doNothing().when(memberValidation).validateKakaoDTO(testKakaoDTO);
		
		// when
		memberHelper.validateKakaoDTO(testKakaoDTO);
		
		// then
		verify(memberValidation, times(1)).validateKakaoDTO(testKakaoDTO);
	}
	
	@Test
	@DisplayName("카카오 콜백 검증 위임")
	void testValidateKakaoCallback() {
		// given
		doNothing().when(memberValidation).validateKakaoCallback(request);
		
		// when
		memberHelper.validateKakaoCallback(request);
		
		// then
		verify(memberValidation, times(1)).validateKakaoCallback(request);
	}
	
	@Test
	@DisplayName("회원 ID로 조회 위임")
	void testFindMemberById() {
		// given
		UUID memberId = testMember.getMemberId();
		when(memberUtil.findMemberById(memberId)).thenReturn(testMember);
		
		// when
		Member result = memberHelper.findMemberById(memberId);
		
		// then
		assertNotNull(result);
		assertEquals(testMember.getMemberId(), result.getMemberId());
		verify(memberUtil, times(1)).findMemberById(memberId);
	}
	
	@Test
	@DisplayName("회원 이메일로 조회 위임")
	void testFindMemberByEmail() {
		// given
		String email = "test@example.com";
		when(memberUtil.findMemberByEmail(email)).thenReturn(testMember);
		
		// when
		Member result = memberHelper.findMemberByEmail(email);
		
		// then
		assertNotNull(result);
		assertEquals(email, result.getEmail());
		verify(memberUtil, times(1)).findMemberByEmail(email);
	}
	
	@Test
	@DisplayName("회원 저장 위임")
	void testSaveMember() {
		// given
		when(memberUtil.save(testMember)).thenReturn(testMember);
		
		// when
		Member result = memberHelper.save(testMember);
		
		// then
		assertNotNull(result);
		assertEquals(testMember.getMemberId(), result.getMemberId());
		verify(memberUtil, times(1)).save(testMember);
	}
	
	@Test
	@DisplayName("회원 삭제 위임")
	void testDeleteMember() {
		// given
		doNothing().when(memberUtil).delete(testMember);
		
		// when
		memberHelper.delete(testMember);
		
		// then
		verify(memberUtil, times(1)).delete(testMember);
	}
	
	@Test
	@DisplayName("회원 존재 여부 확인 위임")
	void testExistsByEmail() {
		// given
		String email = "test@example.com";
		when(memberUtil.existsByEmail(email)).thenReturn(true);
		
		// when
		boolean result = memberHelper.existsByEmail(email);
		
		// then
		assertTrue(result);
		verify(memberUtil, times(1)).existsByEmail(email);
	}
}