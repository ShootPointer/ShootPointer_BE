package com.midas.shootpointer.domain.notification.business.command;

import com.midas.shootpointer.domain.notification.business.FcmManager;
import com.midas.shootpointer.domain.notification.dto.request.FcmTokenRequestDto;
import com.midas.shootpointer.domain.notification.dto.request.SendNotificationRequestDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FcmCommandServiceImpl implements FcmCommandService {
	
	private final FcmManager fcmManager;
	
	@Override
	public void registerToken(FcmTokenRequestDto requestDto) {
		fcmManager.registerToken(requestDto.getMemberId(), requestDto.getTargetToken());
	}
	
	@Override
	public void sendNotification(SendNotificationRequestDto requestDto) {
		fcmManager.sendNotification(
			requestDto.getMemberId(),
			requestDto.getTitle(),
			requestDto.getBody()
		);
	}
	
	@Override
	public boolean deleteToken(UUID memberId) {
		return fcmManager.deleteToken(memberId);
	}
}
