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
public class SendNotificationRequestDto {
	
	@NotNull(message = "회원 ID는 필수입니다.")
	private UUID memberId;
	
	@NotBlank(message = "알림 제목은 필수입니다.")
	private String title;
	
	@NotBlank(message = "알림 내용은 필수입니다.")
	private String body;
}
