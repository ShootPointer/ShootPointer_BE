package com.midas.shootpointer.domain.member.business;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;

public interface MemberManager {

    KakaoDTO processKakaoLogin(String code);
}
