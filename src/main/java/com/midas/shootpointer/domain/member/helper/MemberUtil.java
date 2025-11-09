package com.midas.shootpointer.domain.member.helper;

import com.midas.shootpointer.domain.member.entity.Member;
import java.util.UUID;

public interface MemberUtil {
	
	Member findMemberById(UUID memberId);
	
	Member findMemberByEmail(String email);
	
	Member save(Member member);
	
	void delete(Member member);
	
	boolean existsByEmail(String email);
	
	Integer getBackNumber(UUID memberId);
	
	Integer getTotalTwoPointCount(UUID memberId);
	
	Integer getTotalThreePointCount(UUID memberId);
	
	Integer getHighlightCount(UUID memberId);
}
