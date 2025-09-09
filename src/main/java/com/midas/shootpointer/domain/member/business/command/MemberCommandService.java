package com.midas.shootpointer.domain.member.business.command;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

public interface MemberCommandService {
    
    KakaoDTO processKakaoLogin(HttpServletRequest request);

    UUID deleteMember(UUID memberId);
    
}
