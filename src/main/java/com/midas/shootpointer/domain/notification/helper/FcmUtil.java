package com.midas.shootpointer.domain.notification.helper;

import com.google.firebase.messaging.Message;
import java.util.UUID;

public interface FcmUtil {
	
	/*
	FCM 토큰 저장
	 */
	void saveToken(UUID memberId, String token);
	
	/*
	memberId로 FCM 토큰 조회
	 */
	String getTokenByMemberId(UUID memberId);
	
	/*
	FCM 메세지 작성 -> 해당 토큰에 알림 제목, 알림 내용 작성하기
	 */
	Message createMessage(String title, String body, String token);
	
	/*
	FCM 메세지 클라이언트로 전송
	 */
	void sendMessage(Message message);
	
	/*
	FCM 토큰 존재 여부 확인
	 */
	boolean existsToken(UUID memberId);
	
	/*
	FCM 토큰 삭제
	 */
	boolean deleteToken(UUID memberId);
}
