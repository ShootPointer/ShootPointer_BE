package com.midas.shootpointer.domain.notification.controller;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.notification.business.command.FcmCommandService;
import com.midas.shootpointer.domain.notification.dto.request.FcmTokenRequestDto;
import com.midas.shootpointer.domain.notification.dto.request.SendNotificationRequestDto;
import com.midas.shootpointer.global.dto.ApiResponse;
import com.midas.shootpointer.global.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
@Tag(name = "FCM Noti", description = "FCM 알림 API")
public class FcmCommandController {
	
	private final FcmCommandService fcmCommandService;
	
	@Operation(
		summary = "FCM 토큰 등록 API - [담당자 : 박재성]",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "FCM 토큰 등록 성공"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX", description = "FCM 토큰 등록 실패", content = @Content(mediaType = "application/json"))
		}
	)
	@PostMapping("/register/token")
	public ResponseEntity<?> registerToken(@Valid @RequestBody FcmTokenRequestDto requestDto) {
		fcmCommandService.registerToken(requestDto);
		return ResponseEntity.ok(ApiResponse.ok(requestDto));
	}
	
	@Operation(
		summary = "알림 전송 API - [담당자 : 박재성]",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알림 전송 성공"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4xx", description = "알림 전송 실패", content = @Content(mediaType = "application/json"))
		}
	)
	@PostMapping("/send")
	public ResponseEntity<?> sendNotification(
		@Valid @RequestBody SendNotificationRequestDto requestDto) {
		fcmCommandService.sendNotification(requestDto);
		return ResponseEntity.ok(ApiResponse.ok(requestDto));
	}
	
	@Operation(
		summary = "FCM 토큰 삭제 API - [담당자 : 박재성]",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "FCM 토큰 삭제 성공"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4xx", description = "FCM 토큰 삭제 실패",
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
		}
	)
	@DeleteMapping("/token")
	public ResponseEntity<?> deleteToken() {
		Member member = SecurityUtils.getCurrentMember();
		
		return ResponseEntity.ok(ApiResponse.ok(fcmCommandService.deleteToken(member.getMemberId())));
	}
}
