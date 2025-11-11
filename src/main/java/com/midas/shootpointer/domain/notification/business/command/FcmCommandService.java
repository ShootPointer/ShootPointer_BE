package com.midas.shootpointer.domain.notification.business.command;

import com.midas.shootpointer.domain.notification.dto.request.FcmTokenRequestDto;
import com.midas.shootpointer.domain.notification.dto.request.SendNotificationRequestDto;
import java.util.UUID;

public interface FcmCommandService {
	
	/**
	 * FCM 토큰 등록 (memberId, token)을 매핑
	 * @param requestDto
	 */
	void registerToken(FcmTokenRequestDto requestDto);
	
	/**
	 * 알림 전송
	 * @param requestDto
	 */
	void sendNotification(SendNotificationRequestDto requestDto);
	
	/**
	 * FCM 토큰 삭제
	 * @param memberId
	 */
	boolean deleteToken(UUID memberId);
	
}
