package com.midas.shootpointer.domain.notification.dto;

import java.util.UUID;
import lombok.Getter;

@Getter
public class FcmTokenRequestDto {
	private String targetToken;
	private UUID memberId;
	private String title;
	private String body;
}
