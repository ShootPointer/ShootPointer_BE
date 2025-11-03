package com.midas.shootpointer.domain.notification.controller;

import com.midas.shootpointer.domain.notification.dto.FcmTestRequestDto;
import com.midas.shootpointer.domain.notification.service.FirebaseCloudMessageService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FcmController {
	
	private final FirebaseCloudMessageService firebaseCloudMessageService;
	
	@PostMapping("/fcm")
	public ResponseEntity pushMessage(@RequestBody FcmTestRequestDto requestDto) throws IOException {
		System.out.println(requestDto.getTargetToken() + " " + requestDto.getTitle() + " " + requestDto.getBody());
		
		firebaseCloudMessageService.sendMessageTo(
			requestDto.getTargetToken(),
			requestDto.getTitle(),
			requestDto.getBody()
		);
		
		return ResponseEntity.ok().build();
	}
}
