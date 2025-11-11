package com.midas.shootpointer.domain.notification.helper;

import com.google.firebase.messaging.Message;
import java.util.UUID;

public interface FcmHelper extends FcmValidation, FcmUtil {
	
	void saveToken(UUID memberId, String token);
	
	String getTokenByMemberId(UUID memberId);
	
	Message createMessage(String title, String body, String token);
	
	void sendMessage(Message message);
	
	boolean existsToken(UUID memberId);
	
	void deleteToken(UUID memberId);
}
