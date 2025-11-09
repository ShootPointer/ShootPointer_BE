package com.midas.shootpointer.domain.notification.controller;

import com.midas.shootpointer.domain.notification.dto.FcmTokenRequestDto;
import com.midas.shootpointer.domain.notification.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FcmController {
	
	private final FcmService fcmService;
	
	@PostMapping("/fcm/register/token")
	public void registerToken(@RequestBody FcmTokenRequestDto requestDto) {
		fcmService.registerToken(requestDto.getMemberId(), requestDto.getTargetToken());
	}
	
	@PostMapping("/fcm/send")
	public void sendNotification(@RequestBody FcmTokenRequestDto requestDto) {
		fcmService.sendNotification(
			requestDto.getMemberId(),
			requestDto.getTitle(),
			requestDto.getBody()
		);
	}
}
