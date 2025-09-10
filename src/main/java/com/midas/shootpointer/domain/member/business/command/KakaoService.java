package com.midas.shootpointer.domain.member.business.command;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;

public interface KakaoService {
    KakaoDTO getKakaoUserInfo(String code);
    String getKakaoAccessToken(String code);
    KakaoDTO getUserInfoWithToken(String accessToken);
}
