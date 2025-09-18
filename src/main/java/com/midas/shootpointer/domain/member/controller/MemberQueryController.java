package com.midas.shootpointer.domain.member.controller;

import com.midas.shootpointer.domain.member.business.query.MemberQueryService;
import com.midas.shootpointer.domain.member.dto.MemberResponseDto;
import com.midas.shootpointer.domain.member.mapper.MemberMapper;
import com.midas.shootpointer.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "회원 조회", description = "회원 조회 API")
public class MemberQueryController {
	
	//* TODO : 회원 조회 API는 삭제 예정
	
	private final MemberQueryService memberQueryService;
	private final MemberMapper memberMapper;
	
	/**
	 * 이메일을 통한 회원 정보 조회
	 *
	 * @param email
	 * @return
	 */
	@GetMapping("/email/{email}")
	public ResponseEntity<ApiResponse<MemberResponseDto>> getMemberByEmail(@PathVariable String email) {
		return memberQueryService.findByEmail(email)
			.map(member -> {
				MemberResponseDto dto = memberMapper.entityToDto(member);
				return ResponseEntity.ok(ApiResponse.ok(dto));
			})
			.orElse(ResponseEntity.notFound().build());
	}
}
