package com.midas.shootpointer.domain.member.controller.command;

import com.midas.shootpointer.domain.member.business.command.MemberCommandService;
import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.entity.MsgEntity;
import com.midas.shootpointer.global.annotation.CustomLog;
import com.midas.shootpointer.global.dto.ApiResponse;
import com.midas.shootpointer.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao")
@Tag(name = "카카오 로그인 및 탈퇴", description = "카카오 로그인/탈퇴 API")
public class MemberCommandController {
    
    private final MemberCommandService memberCommandService;
	
	@CustomLog("카카오 소셜 로그인 및 JWT 발급")
	@Operation(
		summary = "카카오 소셜 로그인 및 JWT 발급 API - [담당자 : 박재성]",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "카카오 로그인 성공",
				content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = MsgEntity.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX", description = "카카오 로그인 실패",
				content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
		}
	)
    @GetMapping("/callback")
    public ResponseEntity<MsgEntity> callback(HttpServletRequest request) {
        // 모든 비즈니스 로직은 Service Layer에서 처리
        KakaoDTO kakaoInfo = memberCommandService.processKakaoLogin(request);
        return ResponseEntity.ok().body(new MsgEntity("Success", kakaoInfo));
    }
	
	@CustomLog("카카오 회원 탈퇴")
	@Operation(
		summary = "카카오 회원 탈퇴 API - [담당자 : 박재성]",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "카카오 회원 탈퇴 성공",
				content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX", description = "카카오 회원 탈퇴 실패",
				content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
		}
	)
	@DeleteMapping
    public ResponseEntity<ApiResponse<UUID>> deleteMember(@AuthenticationPrincipal
        CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        UUID deletedMemberId = memberCommandService.deleteMember(member);
        return ResponseEntity.ok(ApiResponse.ok(deletedMemberId));
    }

}
