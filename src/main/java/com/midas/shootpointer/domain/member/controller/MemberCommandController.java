package com.midas.shootpointer.domain.member.controller;

import com.midas.shootpointer.domain.member.business.command.MemberCommandService;
import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.MsgEntity;
import com.midas.shootpointer.global.annotation.CustomLog;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao")
@Tag(name = "회원 관리", description = "회원 관리 API")
public class MemberCommandController {
    
    private final MemberCommandService memberCommandService;
    private final JwtUtil jwtUtil;
    
    @CustomLog("카카오 소셜 로그인 및 JWT 발급")
    @GetMapping("/callback")
    public ResponseEntity<MsgEntity> callback(HttpServletRequest request) {
        // 모든 비즈니스 로직은 Service Layer에서 처리
        KakaoDTO kakaoInfo = memberCommandService.processKakaoLogin(request);
        return ResponseEntity.ok().body(new MsgEntity("Success", kakaoInfo));
    }
    
    @CustomLog("회원 탈퇴")
    @DeleteMapping
    public ResponseEntity<MsgEntity> deleteMember(HttpServletRequest request) {
        // JWT에서 memberId 추출
        String token = jwtUtil.resolveToken(request);
        UUID memberId = jwtUtil.getMemberId(token);
        
        UUID deletedMemberId = memberCommandService.deleteMember(memberId);
        
        return ResponseEntity.ok(
            new MsgEntity("회원 탈퇴가 완료되었습니다.", deletedMemberId)
        );
    }
    
}
