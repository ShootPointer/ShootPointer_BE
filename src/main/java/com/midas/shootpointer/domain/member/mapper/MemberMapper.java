package com.midas.shootpointer.domain.member.mapper;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.dto.MemberResponseDto;
import com.midas.shootpointer.domain.member.entity.Member;

public interface MemberMapper {
	
	Member dtoToEntity(KakaoDTO kakaoDTO);
	
	MemberResponseDto entityToDto(Member member);
}
