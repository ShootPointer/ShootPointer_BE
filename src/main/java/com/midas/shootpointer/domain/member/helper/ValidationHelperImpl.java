package com.midas.shootpointer.domain.member.helper;

import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ValidationHelperImpl implements ValidationHelper {
	
	private final Set<String> processingCodes = ConcurrentHashMap.newKeySet();
	
	@Override
	public void validateKakaoCode(String code) {
		if (code == null || code.isBlank()) {
			throw new CustomException(ErrorCode.INVALID_KAKAO_AUTH_CODE);
		}
		
		if (isDuplicateRequest(code)) {
			throw new CustomException(ErrorCode.DUPLICATE_REQUEST);
		}
		
		processingCodes.add(code);
	}
	
	@Override
	public boolean isDuplicateRequest(String code) {
		return processingCodes.contains(code);
	}
	
}
