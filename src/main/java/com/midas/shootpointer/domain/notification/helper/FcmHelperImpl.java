package com.midas.shootpointer.domain.notification.helper;

import com.google.firebase.messaging.Message;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FcmHelperImpl implements FcmHelper {
	
	private final FcmValidation fcmValidation;
	private final FcmUtil fcmUtil;
	
	@Override
	public void validateMemberId(UUID memberId) {
		fcmValidation.validateMemberId(memberId);
	}
	
	@Override
	public void validateToken(String token) {
		fcmValidation.validateToken(token);
	}
	
	@Override
	public void validateNotificationContent(String title, String body) {
		fcmValidation.validateNotificationContent(title, body);
	}
	
	@Override
	public void saveToken(UUID memberId, String token) {
		fcmUtil.saveToken(memberId, token);
	}
	
	@Override
	public String getTokenByMemberId(UUID memberId) {
		return fcmUtil.getTokenByMemberId(memberId);
	}
	
	@Override
	public Message createMessage(String title, String body, String token) {
		return fcmUtil.createMessage(title, body, token);
	}
	
	@Override
	public void sendMessage(Message message) {
		fcmUtil.sendMessage(message);
	}
	
	@Override
	public boolean existsToken(UUID memberId) {
		return fcmUtil.existsToken(memberId);
	}
	
	@Override
	public void deleteToken(UUID memberId) {
		fcmUtil.deleteToken(memberId);
	}
}
