package com.midas.shootpointer.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {
	
	private final FirebaseMessaging firebaseMessaging;
	private final RedisTemplate<String, String> redisTemplate;
	
	private static final String FCM_KEY_PREFIX = "fcm:token:";
	
	public void registerToken(UUID memberId, String token) {
		String key = FCM_KEY_PREFIX + memberId;
		
		redisTemplate.opsForValue().set(key, token);
		
		log.info("Redis에 FCM 토큰 등록 완료 (memberId: {})", memberId);
	}
	
	public void sendNotification(UUID memberId, String title, String body) {
		String token = redisTemplate.opsForValue().get(FCM_KEY_PREFIX + memberId); // 토큰 가져오기
		
		if (token == null) {
			log.warn("토큰이 존재하지 않아 알림을 보낼 수 없습니다. (memberId: {})", memberId);
			return;
		}
		
		log.info("알림 전송 시도 (title: {}, body: {}, fcmToken: {})", memberId, title, body);
		send(createMessage(title, body, token));
	}
	
	private void send(Message message) {
		try {
			String response = firebaseMessaging.send(message);
			log.info("성공적으로 알림을 전송했습니다: {}", response);
		} catch (FirebaseMessagingException e) {
			log.error("알림 전송에 실패했습니다: {}", e.getMessage());
		}
	}
	
	private Message createMessage(String title, String body, String fcmToken) {
		return Message.builder()
			.putData("title", title)
			.putData("body", body)
			.setToken(fcmToken)
			.build();
	}
}
