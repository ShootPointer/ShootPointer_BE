package com.midas.shootpointer.domain.member.controller.query;

import com.midas.shootpointer.domain.member.business.query.MemberQueryService;
import com.midas.shootpointer.domain.member.dto.MemberResponseDto;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.annotation.CustomLog;
import com.midas.shootpointer.global.dto.ApiResponse;
import com.midas.shootpointer.global.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "회원 관리", description = "회원 관리 API")
public class MemberQueryController {

	private final MemberQueryService memberQueryService;
	
	@CustomLog("회원 정보 조회")
	@Operation(
		summary = "회원 정보 조회 API - [담당자 : 박재성]",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 정보 조회 성공",
				content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = MemberResponseDto.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX", description = "회원 정보 조회 실패",
				content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
		}
	)
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<MemberResponseDto>> getCurrentMember() {
		Member currentMember = SecurityUtils.getCurrentMember();
		return  ResponseEntity.ok(ApiResponse.ok(memberQueryService.getMemberInfo(currentMember.getMemberId())));
	}
}
