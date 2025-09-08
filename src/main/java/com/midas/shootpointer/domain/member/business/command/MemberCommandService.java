package com.midas.shootpointer.domain.member.business.command;

import com.midas.shootpointer.domain.member.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

public interface MemberCommandService {
    
    Member processKakaoLogin(HttpServletRequest request);

    UUID deleteMember(UUID memberId, Member currentMember); // 회원 탈퇴는 다음 issue
    
}
