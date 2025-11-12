package com.midas.shootpointer.domain.notification.helper;

import java.util.UUID;

public interface FcmValidation {
	
	/*
	memberId 유효성 검증
	 */
	void validateMemberId(UUID memberId);
	
	/*
	FCM 토큰 유효성 검증
	 */
	void validateToken(String token);
	
	/*
	알림 내용 유효성 검증
	 */
	void validateNotificationContent(String title, String body);
}
