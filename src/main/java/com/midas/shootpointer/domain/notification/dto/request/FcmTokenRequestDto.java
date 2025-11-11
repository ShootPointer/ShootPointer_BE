package com.midas.shootpointer.domain.notification.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FcmTokenRequestDto {
	
	@NotNull(message = "회원 ID는 필수입니다.")
	private UUID memberId;
	
	@NotBlank(message = "FCM 토큰은 필수입니다.")
	private String targetToken;
}
