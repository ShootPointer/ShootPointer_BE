package com.midas.shootpointer.domain.member.helper;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;

public interface KakaoApiHelper {
	String requestAccessToken(String code);
	KakaoDTO requestUserInfo(String accessToken);
}
