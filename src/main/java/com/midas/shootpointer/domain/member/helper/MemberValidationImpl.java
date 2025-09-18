package com.midas.shootpointer.domain.member.helper;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberQueryRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class MemberValidationImpl implements MemberValidation {
	
	private final MemberQueryRepository memberQueryRepository;
	private final Set<String> processingCodes = ConcurrentHashMap.newKeySet();
	
	@Override
	public void validateKakaoDTO(KakaoDTO kakaoDTO) {
		if (kakaoDTO == null) {
			throw new CustomException(ErrorCode.INVALID_KAKAO_USER_INFO);
		}
		
		if (!StringUtils.hasText(kakaoDTO.getEmail())) {
			throw new CustomException(ErrorCode.INVALID_EMAIL);
		}
		
		if (!StringUtils.hasText(kakaoDTO.getNickname())) {
			throw new CustomException(ErrorCode.INVALID_NICKNAME);
		}
		
		validateEmailFormat(kakaoDTO.getEmail());
	}
	
	@Override
	public void validateKakaoCallback(HttpServletRequest request) {
		if (hasKakaoError(request)) {
			throw new CustomException(ErrorCode.KAKAO_AUTH_ERROR);
		}
		
		String code = request.getParameter("code");
		validateKakaoCode(code);
	}
	
	@Override
	public void validateKakaoCode(String code) {
		if (code == null || code.isBlank()) {
			throw new CustomException(ErrorCode.INVALID_KAKAO_AUTH_CODE);
		}
		
		processingCodes.add(code);
	}
	
	@Override
	public void isMemberExists(String email) {
		if (memberQueryRepository.findByEmail(email).isPresent()) {
			throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
		}
	}
	
	@Override
	public void isMemberOwner(Member member, UUID memberId) {
		if (!member.getMemberId().equals(memberId)) {
			throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER_ACCESS);
		}
	}
	
	private void validateEmailFormat(String email) {
		String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
		if (!email.matches(emailRegex)) {
			throw new CustomException(ErrorCode.INVALID_EMAIL_FORMAT);
		}
	}
	
	private boolean hasKakaoError(HttpServletRequest request) {
		return request.getParameter("error") != null;
	}
	
	private boolean isDuplicateRequest(String code) {
		return processingCodes.contains(code);
	}

}
