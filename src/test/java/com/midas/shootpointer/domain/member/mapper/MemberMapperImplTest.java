package com.midas.shootpointer.domain.member.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.dto.MemberResponseDto;
import com.midas.shootpointer.domain.member.entity.Member;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MemberMapper 테스트")
class MemberMapperImplTest {
	
	private MemberMapper memberMapper;
	
	@BeforeEach
	void setUp() {
		memberMapper = new MemberMapperImpl();
	}
	
	@Test
	@DisplayName("KakaoDTO를 Member 엔티티로 변환")
	void testDtoToEntity() {
		// given
		KakaoDTO kakaoDTO = KakaoDTO.builder()
			.email("test@kakao.com")
			.nickname("testUser")
			.build();
		
		// when
		Member member = memberMapper.dtoToEntity(kakaoDTO);
		
		// then
		assertNotNull(member);
		assertEquals(kakaoDTO.getEmail(), member.getEmail());
		assertEquals(kakaoDTO.getNickname(), member.getUsername());
	}
	
	@Test
	@DisplayName("Member 엔티티를 MemberResponseDto로 변환")
	void testEntityToDto() {
		// given
		UUID memberId = UUID.randomUUID();
		Member member = Member.builder()
			.memberId(memberId)
			.email("test@kakao.com")
			.username("testUser")
			.build();
		
		// when
		MemberResponseDto response = memberMapper.entityToDto(member);
		
		// then
		assertNotNull(response);
		assertEquals(member.getMemberId(), response.getMemberId());
		assertEquals(member.getEmail(), response.getEmail());
		assertEquals(member.getUsername(), response.getUsername());
	}
}