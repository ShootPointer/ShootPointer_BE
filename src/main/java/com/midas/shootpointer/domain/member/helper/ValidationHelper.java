package com.midas.shootpointer.domain.member.helper;

public interface ValidationHelper {
	void validateKakaoCode(String code);
	boolean isDuplicateRequest(String code);
}
