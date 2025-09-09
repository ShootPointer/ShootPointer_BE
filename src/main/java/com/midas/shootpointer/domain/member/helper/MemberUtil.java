package com.midas.shootpointer.domain.member.helper;

import com.midas.shootpointer.domain.member.entity.Member;
import java.util.UUID;

public interface MemberUtil {
	
	Member findMemberById(UUID memberId);
	
	Member findMemberByEmail(String email);
	
	Member save(Member member);
	
	void delete(Member member);
	
	boolean existsByEmail(String email);
	
}
