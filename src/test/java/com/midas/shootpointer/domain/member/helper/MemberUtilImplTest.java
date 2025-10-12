package com.midas.shootpointer.domain.member.helper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberCommandRepository;
import com.midas.shootpointer.domain.member.repository.MemberQueryRepository;
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
@DisplayName("MemberUtil 테스트")
class MemberUtilImplTest {
	
	@Mock
	private MemberQueryRepository memberQueryRepository;
	
	@Mock
	private MemberCommandRepository memberCommandRepository;
	
	@InjectMocks
	private MemberUtilImpl memberUtil;
	
	private Member testMember;
	private UUID testMemberId;
	
	@BeforeEach
	void setUp() {
		testMemberId = UUID.randomUUID();
		testMember = Member.builder()
			.memberId(testMemberId)
			.username("testUser")
			.email("test@kakao.com")
			.build();
	}
	
	@Test
	@DisplayName("ID로 회원 조회 - 성공")
	void testFindMemberByIdSuccess() {
		// given
		when(memberQueryRepository.findById(testMemberId)).thenReturn(Optional.of(testMember));
		
		// when
		Member result = memberUtil.findMemberById(testMemberId);
		
		// then
		assertNotNull(result);
		assertEquals(testMemberId, result.getMemberId());
		assertEquals("test@kakao.com", result.getEmail());
		verify(memberQueryRepository, times(1)).findById(testMemberId);
	}
	
	@Test
	@DisplayName("ID로 회원 조회 - 실패 (회원 없음)")
	void testFindMemberByIdNotFound() {
		// given
		when(memberQueryRepository.findById(testMemberId)).thenReturn(Optional.empty());
		
		// when & then
		assertThrows(CustomException.class, () -> memberUtil.findMemberById(testMemberId));
		verify(memberQueryRepository, times(1)).findById(testMemberId);
	}
	
	@Test
	@DisplayName("이메일로 회원 조회 - 성공")
	void testFindMemberByEmailSuccess() {
		// given
		String email = "test@kakao.com";
		when(memberQueryRepository.findByEmail(email)).thenReturn(Optional.of(testMember));
		
		// when
		Member result = memberUtil.findMemberByEmail(email);
		
		// then
		assertNotNull(result);
		assertEquals(email, result.getEmail());
		verify(memberQueryRepository, times(1)).findByEmail(email);
	}
	
	@Test
	@DisplayName("이메일로 회원 조회 - 실패 (회원 없음)")
	void testFindMemberByEmailNotFound() {
		// given
		String email = "nonexistent@kakao.com";
		when(memberQueryRepository.findByEmail(email)).thenReturn(Optional.empty());
		
		// when & then
		assertThrows(CustomException.class, () -> memberUtil.findMemberByEmail(email));
		verify(memberQueryRepository, times(1)).findByEmail(email);
	}
	
	@Test
	@DisplayName("회원 저장")
	void testSaveMember() {
		// given
		when(memberCommandRepository.save(testMember)).thenReturn(testMember);
		
		// when
		Member result = memberUtil.save(testMember);
		
		// then
		assertNotNull(result);
		assertEquals(testMember.getMemberId(), result.getMemberId());
		verify(memberCommandRepository, times(1)).save(testMember);
	}
	
	@Test
	@DisplayName("회원 삭제")
	void testDeleteMember() {
		// given
		doNothing().when(memberCommandRepository).delete(testMember);
		
		// when
		memberUtil.delete(testMember);
		
		// then
		verify(memberCommandRepository, times(1)).delete(testMember);
	}
	
	@Test
	@DisplayName("이메일 존재 여부 확인 - 존재함")
	void testExistsByEmailTrue() {
		// given
		String email = "test@kakao.com";
		when(memberQueryRepository.findByEmail(email)).thenReturn(Optional.of(testMember));
		
		// when
		boolean result = memberUtil.existsByEmail(email);
		
		// then
		assertTrue(result);
		verify(memberQueryRepository, times(1)).findByEmail(email);
	}
	
	@Test
	@DisplayName("이메일 존재 여부 확인 - 존재하지 않음")
	void testExistsByEmailFalse() {
		// given
		String email = "nonexistent@kakao.com";
		when(memberQueryRepository.findByEmail(email)).thenReturn(Optional.empty());
		
		// when
		boolean result = memberUtil.existsByEmail(email);
		
		// then
		assertFalse(result);
		verify(memberQueryRepository, times(1)).findByEmail(email);
	}
}