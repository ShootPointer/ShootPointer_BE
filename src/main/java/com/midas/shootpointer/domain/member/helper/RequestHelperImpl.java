package com.midas.shootpointer.domain.member.helper;

import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RequestHelperImpl implements RequestHelper {
	
	@Override
	public String validateAndExtractKakaoCallback(HttpServletRequest request) {
		// 요청 정보 로깅
		logRequestDetails(request);
		
		// 에러 체크
		if (hasKakaoError(request)) {
			String errorDetails = getKakaoErrorDetails(request);
			System.err.println(errorDetails);
			throw new RuntimeException("카카오 에러 반환: " + errorDetails);
		}
		
		// 코드 추출 및 검증
		String code = extractKakaoCode(request);
		if (code == null || code.isBlank()) {
			throw new CustomException(ErrorCode.INVALID_KAKAO_AUTH_CODE);
		}
		
		return code;
	}
	
	@Override
	public void logRequestDetails(HttpServletRequest request) {
		System.err.println("=== KAKAO CALLBACK DEBUG ===");
		System.err.println("Code: " + request.getParameter("code"));
		System.err.println("Error: " + request.getParameter("error"));
		System.err.println("All parameters: " + request.getParameterMap());
		System.err.println("Request URL: " + request.getRequestURL());
		System.err.println("Query String: " + request.getQueryString());
		System.err.println("===========================");
	}
	
	@Override
	public boolean hasKakaoError(HttpServletRequest request) {
		return request.getParameter("error") != null;
	}
	
	@Override
	public String getKakaoErrorDetails(HttpServletRequest request) {
		String error = request.getParameter("error");
		String errorDescription = request.getParameter("error_description");
		return String.format("Kakao OAuth Error: %s - %s", error, errorDescription);
	}
	
	@Override
	public String extractKakaoCode(HttpServletRequest request) {
		return request.getParameter("code");
	}
}
