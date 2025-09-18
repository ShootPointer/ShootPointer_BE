package com.midas.shootpointer.domain.member.mapper;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.dto.MemberResponseDto;
import com.midas.shootpointer.domain.member.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapperImpl implements MemberMapper {
	
	@Override
	public Member dtoToEntity(KakaoDTO kakaoDTO) {
		return Member.builder()
			.email(kakaoDTO.getEmail())
			.username(kakaoDTO.getNickname())
			.build();
	}
	
	@Override
	public MemberResponseDto entityToDto(Member member) {
		return MemberResponseDto.builder()
			.memberId(member.getMemberId())
			.email(member.getEmail())
			.username(member.getUsername())
			.build();
	}
}
