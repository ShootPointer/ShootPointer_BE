package com.midas.shootpointer.domain.member.business.command;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;

public interface MemberService {

    Member findOrCreateMember(KakaoDTO kakaoDTO);
    Member createNewMember(KakaoDTO kakaoDTO);
}
