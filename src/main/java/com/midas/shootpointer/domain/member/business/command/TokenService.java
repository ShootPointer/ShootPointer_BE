package com.midas.shootpointer.domain.member.business.command;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;

public interface TokenService {
    void generateTokens(Member member, KakaoDTO kakaoDTO);
}
