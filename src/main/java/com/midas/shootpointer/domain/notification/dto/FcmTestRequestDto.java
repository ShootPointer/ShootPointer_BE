package com.midas.shootpointer.domain.notification.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 현재 FCM을 테스트하기 위한 DTO로, 추후에 FE에서 토큰과 title, body를 넘겨줘야함.
 * 테스트 용도로 RequestDto를 만들었고, FE에서 넘겨주게 된다면 Redis에 컬럼을 저장해서 구현할 예정.
 */
@Getter
@Setter
public class FcmTestRequestDto {
	private String targetToken;
	private String title;
	private String body;
}
