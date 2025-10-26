package com.midas.shootpointer.domain.member.business;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.helper.MemberHelper;
import com.midas.shootpointer.domain.member.mapper.MemberMapper;
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
@DisplayName("MemberManager 테스트")
class MemberManagerTest {
	
	@Mock
	private MemberHelper memberHelper;
	
	@Mock
	private MemberMapper memberMapper;
	
	@Mock
	private HttpServletRequest request;
	
	@InjectMocks
	private MemberManager memberManager;
	
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
	@DisplayName("카카오 콜백 검증")
	void testValidateKakaoCallback() {
		// given
		doNothing().when(memberHelper).validateKakaoCallback(request);
		
		// when
		memberManager.validateKakaoCallback(request);
		
		// then
		verify(memberHelper, times(1)).validateKakaoCallback(request);
	}
	
	@Test
	@DisplayName("카카오 로그인 처리 - 기존 회원")
	void testProcessKakaoLoginExistingMember() {
		// given
		doNothing().when(memberHelper).validateKakaoCallback(request);
		doNothing().when(memberHelper).validateKakaoDTO(testKakaoDTO);
		when(memberHelper.findMemberByEmail(testKakaoDTO.getEmail())).thenReturn(testMember);
		
		// when
		Member result = memberManager.processKakaoLogin(request, testKakaoDTO);
		
		// then
		assertNotNull(result);
		assertEquals(testMember.getMemberId(), result.getMemberId());
		verify(memberHelper, times(1)).validateKakaoCallback(request);
		verify(memberHelper, times(1)).validateKakaoDTO(testKakaoDTO);
	}
	
	@Test
	@DisplayName("카카오 로그인 처리 - 신규 회원 생성")
	void testProcessKakaoLoginNewMember() {
		// given
		doNothing().when(memberHelper).validateKakaoCallback(request);
		doNothing().when(memberHelper).validateKakaoDTO(testKakaoDTO);
		when(memberHelper.findMemberByEmail(testKakaoDTO.getEmail()))
			.thenThrow(new RuntimeException("Member not found"));
		when(memberMapper.dtoToEntity(testKakaoDTO)).thenReturn(testMember);
		when(memberHelper.save(testMember)).thenReturn(testMember);
		
		// when
		Member result = memberManager.processKakaoLogin(request, testKakaoDTO);
		
		// then
		assertNotNull(result);
		assertEquals(testMember.getMemberId(), result.getMemberId());
		verify(memberMapper, times(1)).dtoToEntity(testKakaoDTO);
		verify(memberHelper, times(1)).save(testMember);
	}
	
	@Test
	@DisplayName("회원 삭제")
	void testDeleteMember() {
		// given
		UUID memberId = testMember.getMemberId();
		doNothing().when(memberHelper).isMemberOwner(testMember, memberId);
		when(memberHelper.findMemberById(memberId)).thenReturn(testMember);
		doNothing().when(memberHelper).delete(testMember);
		
		// when
		UUID result = memberManager.deleteMember(memberId, testMember);
		
		// then
		assertNotNull(result);
		assertEquals(memberId, result);
		verify(memberHelper, times(1)).isMemberOwner(testMember, memberId);
		verify(memberHelper, times(1)).findMemberById(memberId);
		verify(memberHelper, times(1)).delete(testMember);
	}
	
	@Test
	@DisplayName("회원 ID로 조회")
	void testFindMemberById() {
		// given
		UUID memberId = testMember.getMemberId();
		when(memberHelper.findMemberById(memberId)).thenReturn(testMember);
		
		// when
		Member result = memberManager.findMemberById(memberId);
		
		// then
		assertNotNull(result);
		assertEquals(testMember.getMemberId(), result.getMemberId());
		verify(memberHelper, times(1)).findMemberById(memberId);
	}

	@Test
	@DisplayName("회원의 하이라이트 정보 수집 여부 값을 true로 변환합니다.")
	void agree(){
		//given
		assertEquals(false,testMember.getIsAggregationAgreed());

		//when
		UUID result=memberManager.agree(testMember);

		//then
		assertNotNull(result);
		assertEquals(testMember.getMemberId(),result);
		assertEquals(true,testMember.getIsAggregationAgreed());
	}
}