package com.midas.shootpointer.domain.member.business.command;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.helper.KakaoApiHelper;
import com.midas.shootpointer.global.annotation.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoServiceImpl implements KakaoService {

	private final KakaoApiHelper kakaoApiHelper;

	@Override
	@CustomLog("== 카카오 로그인 Process Start ==")
	public KakaoDTO getKakaoUserInfo(String code) {
		String accessToken = getKakaoAccessToken(code);
		return getUserInfoWithToken(accessToken);
	}

	@Override
	@CustomLog("== 카카오 Access Token 발급 ==")
	public String getKakaoAccessToken(String code) {
		return kakaoApiHelper.requestAccessToken(code);
	}

	@Override
	@CustomLog("== 카카오에 사용자 정보 요청하기 ==")
	public KakaoDTO getUserInfoWithToken(String accessToken) {
		return kakaoApiHelper.requestUserInfo(accessToken);
	}
}
