package com.midas.shootpointer.domain.member.business.command;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

public interface MemberCommandService {
    
    KakaoDTO processKakaoLogin(HttpServletRequest request);

//    UUID deleteMember(HttpServletRequest request);
    
    UUID deleteMember(Member member);
}
