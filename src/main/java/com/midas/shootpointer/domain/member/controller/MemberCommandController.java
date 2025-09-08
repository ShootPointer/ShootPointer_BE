package com.midas.shootpointer.domain.member.controller;

import com.midas.shootpointer.domain.member.business.MemberManager;
import com.midas.shootpointer.domain.member.business.command.KakaoService;
import com.midas.shootpointer.domain.member.business.command.MemberCommandService;
import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.entity.MsgEntity;
import com.midas.shootpointer.global.annotation.CustomLog;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao")
@Tag(name = "회원 관리", description = "회원 관리 API")
public class MemberCommandController {
    
    private final MemberCommandService memberCommandService;
    
    @CustomLog("카카오 소셜 로그인 및 JWT 발급")
    @GetMapping("/callback")
    public ResponseEntity<MsgEntity> callback(HttpServletRequest request) {
        // 모든 비즈니스 로직은 Service Layer에서 처리
        KakaoDTO kakaoInfo = memberCommandService.processKakaoLogin(request);
        return ResponseEntity.ok().body(new MsgEntity("Success", kakaoInfo));
    }
    
}
