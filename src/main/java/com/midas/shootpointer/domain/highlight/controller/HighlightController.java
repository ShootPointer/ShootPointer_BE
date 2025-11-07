package com.midas.shootpointer.domain.highlight.controller;

import com.midas.shootpointer.domain.highlight.business.command.HighlightCommandService;
import com.midas.shootpointer.domain.highlight.dto.HighlightRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.dto.ApiResponse;
import com.midas.shootpointer.global.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/highlight")
@RequiredArgsConstructor
@Tag(name = "하이라이트 URL 전송 / 하이라이트 선택")
public class HighlightController {
    private final HighlightCommandService highlightCommandService;

    @PostMapping("/select")
    public ResponseEntity<ApiResponse<HighlightSelectResponse>> selectHighlight(
            @RequestBody HighlightSelectRequest request
    ) {
        Member member = SecurityUtils.getCurrentMember();

        return ResponseEntity.ok(ApiResponse.ok(highlightCommandService.selectHighlight(request, member)));
    }

    @Operation(
            summary = "하이라이트 URL 전송 API - [담당자 : 김도연]",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "전송 성공",
                            content = @Content(mediaType="application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX",description = "전송 실패",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = com.midas.shootpointer.global.dto.ApiResponse.class)))
            }
    )
    @PostMapping("/upload-result")
    public ResponseEntity<ApiResponse<?>> uploadHighlights(
            @RequestHeader("X-Member-Id") UUID memberId,
            @RequestBody HighlightRequest highlights
    ) {
        highlightCommandService.uploadHighlights(highlights,memberId);
        return ResponseEntity.ok(ApiResponse.okWithOutData());
    }
}
