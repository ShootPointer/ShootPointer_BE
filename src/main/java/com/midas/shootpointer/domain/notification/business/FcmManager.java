package com.midas.shootpointer.domain.notification.business;

import com.google.firebase.messaging.Message;
import com.midas.shootpointer.domain.notification.helper.FcmHelper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmManager {
	
	private final FcmHelper fcmHelper;
	
	@Transactional
	public void registerToken(UUID memberId, String token) {
		log.info("| ==== FCM 토큰 등록 ==== | memberId : {}, token : {}", memberId, token);
		// validation 검증
		fcmHelper.validateMemberId(memberId);
		fcmHelper.validateToken(token);
		// FCM 토큰 저장 (memberId랑 token 매핑해서)
		fcmHelper.saveToken(memberId, token);
		
		log.info("| ==== FCM 토큰 등록 성공 ==== | memberId : {}, token : {}", memberId, token);
	}
	
	@Transactional
	public void sendNotification(UUID memberId, String title, String body) {
		log.info("| ==== 알림 전송 시작 ==== | memberId : {}, title : {}, body : {}", memberId, title, body);
		// validation 검증
		fcmHelper.validateMemberId(memberId);
		fcmHelper.validateNotificationContent(title, body);
		// FCM 토큰 조회
		String token = fcmHelper.getTokenByMemberId(memberId);
		// FCM 메세지 생성 후 전송
		Message message = fcmHelper.createMessage(title, body, token);
		fcmHelper.sendMessage(message);
		
		log.info("| ==== 알림 전송 완료 ==== | memberId : {}, message : {}", memberId, message);
	}
	
	@Transactional
	public boolean deleteToken(UUID memberId) {
		// validation 검증
		fcmHelper.validateMemberId(memberId);
		fcmHelper.existsToken(memberId);
		// FCM 토큰 삭제
		boolean isDeletedToken = fcmHelper.deleteToken(memberId);
		log.info("| ==== FCM 토큰 삭제 결과 ==== | memberId : {}, isDeleted : {}", memberId, isDeletedToken);
		
		return isDeletedToken;
	}
}
