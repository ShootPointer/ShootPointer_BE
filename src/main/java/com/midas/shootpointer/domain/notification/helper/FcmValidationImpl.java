package com.midas.shootpointer.domain.notification.helper;

import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FcmValidationImpl implements FcmValidation {
	
	private static final int MAX_TITLE_LENGTH = 100;
	private static final int MAX_BODY_LENGTH = 1000;
	
	@Override
	public void validateMemberId(UUID memberId) {
		if (memberId == null) {
			throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
		}
	}
	
	@Override
	public void validateToken(String token) {
		if (token == null || token.isBlank()) {
			throw new CustomException(ErrorCode.FCM_TOKEN_NOT_FOUND);
		}
	}
	
	@Override
	public void validateNotificationContent(String title, String body) {
		if (title == null || title.isBlank()) {
			throw new CustomException(ErrorCode.FCM_TITLE_NOT_FOUND);
		}
		
		if (body == null || body.isBlank()) {
			throw new CustomException(ErrorCode.FCM_BODY_NOT_FOUND);
		}
		
		if (title.length() > MAX_TITLE_LENGTH) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}
		
		if (body.length() > MAX_BODY_LENGTH) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}
	}
}
