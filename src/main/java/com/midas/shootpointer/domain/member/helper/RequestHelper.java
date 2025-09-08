package com.midas.shootpointer.domain.member.helper;

import jakarta.servlet.http.HttpServletRequest;

public interface RequestHelper {
	String validateAndExtractKakaoCallback(HttpServletRequest request);
	boolean hasKakaoError(HttpServletRequest request);
	String getKakaoErrorDetails(HttpServletRequest request);
	String extractKakaoCode(HttpServletRequest request);
}
