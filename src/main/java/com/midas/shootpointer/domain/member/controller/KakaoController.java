package com.midas.shootpointer.domain.member.controller;

import com.midas.shootpointer.domain.member.business.MemberManager;
import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.MsgEntity;
import com.midas.shootpointer.domain.member.helper.RequestHelper;
import com.midas.shootpointer.global.annotation.CustomLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao")
public class KakaoController {
    
    private final MemberManager memberManager;
    private final RequestHelper requestHelper;
    
    @CustomLog("카카오 소셜 로그인 및 JWT 발급")
    @GetMapping("/callback")
    public ResponseEntity<MsgEntity> callback(HttpServletRequest request) {
        // HTTP 요청 검증 및 코드 추출
        String code = requestHelper.validateAndExtractKakaoCallback(request);
        
        // 비즈니스 로직 처리 (JWT 토큰 생성 포함)
        KakaoDTO kakaoInfo = memberManager.processKakaoLogin(code);
        
        return ResponseEntity.ok().body(new MsgEntity("Success", kakaoInfo));
    }
}
