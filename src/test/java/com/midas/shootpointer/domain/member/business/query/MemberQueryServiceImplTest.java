package com.midas.shootpointer.domain.member.business.query;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.helper.MemberHelper;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
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
@DisplayName("MemberQueryService 테스트")
class MemberQueryServiceImplTest {
	
	@Mock
	private MemberHelper memberHelper;
	
	@InjectMocks
	private MemberQueryServiceImpl memberQueryService;
	
	private Member testMember;
	
	@BeforeEach
	void setUp() {
		testMember = Member.builder()
			.memberId(UUID.randomUUID())
			.username("testUser")
			.email("test@kakao.com")
			.build();
	}
	
	@Test
	@DisplayName("이메일로 회원 조회 - 성공")
	void testFindByEmailSuccess() {
		// given
		String email = "test@kakao.com";
		when(memberHelper.findMemberByEmail(email)).thenReturn(testMember);
		
		// when
		Optional<Member> result = memberQueryService.findByEmail(email);
		
		// then
		assertTrue(result.isPresent());
		assertEquals(testMember.getEmail(), result.get().getEmail());
		verify(memberHelper, times(1)).findMemberByEmail(email);
	}
	
	@Test
	@DisplayName("이메일로 회원 조회 - 실패 (회원 없음)")
	void testFindByEmailFailure() {
		// given
		String email = "nonexistent@kakao.com";
		when(memberHelper.findMemberByEmail(email)).thenThrow(new CustomException(ErrorCode.MEMBER_NOT_FOUND));
		
		// when
		Optional<Member> result = memberQueryService.findByEmail(email);
		
		// then
		assertTrue(result.isEmpty());
		verify(memberHelper, times(1)).findMemberByEmail(email);
	}
}