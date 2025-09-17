package com.midas.shootpointer.global.security;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 어느 패키지 / 클래스에서 인증된 Member 객체를 사용할 수 있는 Security Utils 클래스
 */

@Component
public class SecurityUtils {
	
	/**
	 * Member 객체 반환하는 메서드
	 * @return Member
	 */
	public static Member getCurrentMember() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication != null &&
			authentication.isAuthenticated() &&
			authentication.getPrincipal() instanceof CustomUserDetails) {
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal(); // Object 타입을 반환하는데, 이를 CustomUserDetails로 Boxing
			
			return userDetails.getMember();
		}
		
		throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER_ACCESS);
		// 현재 인증된 Member 객체를 가져오지 못한다면 인증이 만료되었거나, 존재하지 않기 때문에 두 케이스 전부 인증되지 않은 것으로 간주
	}
	
	/**
	 * Member 객체의 해당 memberId를 가져오는 메서드
	 * @return UUID
	 */
	public static UUID getCurrentMemberId() {
		return getCurrentMember().getMemberId();
	}
	
	// Member 객체의 이메일을 가져오는 메서드는 구현 필요시 구현할게요

}
