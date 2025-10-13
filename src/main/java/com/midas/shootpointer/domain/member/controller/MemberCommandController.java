package com.midas.shootpointer.domain.member.controller;

import com.midas.shootpointer.domain.member.business.command.MemberCommandService;
import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.dto.MemberResponseDto;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.entity.MsgEntity;
import com.midas.shootpointer.global.annotation.CustomLog;
import com.midas.shootpointer.global.dto.ApiResponse;
import com.midas.shootpointer.global.security.CustomUserDetails;
import com.midas.shootpointer.global.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao") // RequestMapping API 엔드포인트 수정할 필요가 있어보임,,, -> 카카오 디벨로퍼스에서도 수정해야함
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
    
    @CustomLog("회원 탈퇴")
    @DeleteMapping
    public ResponseEntity<ApiResponse<UUID>> deleteMember(@AuthenticationPrincipal
        CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        UUID deletedMemberId = memberCommandService.deleteMember(member);
        return ResponseEntity.ok(ApiResponse.ok(deletedMemberId));
    }
    
    @CustomLog("회원 정보 조회")
    @GetMapping("/me")
    public MemberResponseDto getCurrentMember() {
        Member currentMember = SecurityUtils.getCurrentMember();
		
		return MemberResponseDto.builder()
			.memberId(currentMember.getMemberId())
			.email(currentMember.getEmail())
			.username(currentMember.getUsername())
			.build();
    }

    @PutMapping("/highlight/agree")
    public ResponseEntity<ApiResponse<UUID>> agree(){
        Member currentMember=SecurityUtils.getCurrentMember();
        return ResponseEntity.ok(ApiResponse.ok(memberCommandService.agree(currentMember)));
    }
}
