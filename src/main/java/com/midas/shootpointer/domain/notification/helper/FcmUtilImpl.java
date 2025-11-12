package com.midas.shootpointer.domain.notification.helper;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmUtilImpl implements FcmUtil {
	
	private static final String FCM_KEY_PREFIX = "fcm:token:"; // Redis에 저장될 Prefix 구분값
	
	private final FirebaseMessaging firebaseMessaging;
	private final RedisTemplate<String, String> redisTemplate;
	
	@Override
	public void saveToken(UUID memberId, String token) {
		String key = generateKey(memberId);
		redisTemplate.opsForValue().set(key, token);
		log.info("FCM Token 저장 완료 - memberId : {}, Token : {}", memberId, token);
	}
	
	@Override
	public String getTokenByMemberId(UUID memberId) {
		String key = generateKey(memberId);
		String token = redisTemplate.opsForValue().get(key); // Redis에 저장되어있는 토큰 가져오기
		
		if (token == null) {
			throw new CustomException(ErrorCode.FCM_TOKEN_NOT_FOUND);
		}
		return token;
	}
	
	@Override
	public Message createMessage(String title, String body, String token) {
		return Message.builder()
			.setNotification(
				Notification.builder()
					.setTitle(title)
					.setBody(body)
					.build()
			)
			.putData("title", title)
			.putData("body", body)
			.setToken(token)
			.build();
	}
	
	@Override
	public void sendMessage(Message message) {
		try {
			String response = firebaseMessaging.send(message);
			log.info("FCM 메세지 전송 성공 : {}", response);
		} catch (FirebaseMessagingException e) {
			log.error("FCM 메세지 전송 실패 : {}", e.getMessage(), e);
			throw new CustomException(ErrorCode.FCM_SEND_FAILED);
		}
	}
	
	@Override
	public boolean existsToken(UUID memberId) {
		return redisTemplate.hasKey(generateKey(memberId));
	}
	
	@Override
	public boolean deleteToken(UUID memberId) {
		return redisTemplate.delete(generateKey(memberId));
	}
	
	private String generateKey(UUID memberId) {
		return FCM_KEY_PREFIX + memberId.toString();
	}
}
