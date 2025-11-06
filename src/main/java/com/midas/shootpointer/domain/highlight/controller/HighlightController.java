package com.midas.shootpointer.domain.highlight.controller;

import com.midas.shootpointer.domain.highlight.business.command.HighlightCommandService;
import com.midas.shootpointer.domain.highlight.dto.HighlightRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.dto.ApiResponse;
import com.midas.shootpointer.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/highlight")
@RequiredArgsConstructor
public class HighlightController {
    private final HighlightCommandService highlightCommandService;

    @PostMapping("/select")
    public ResponseEntity<ApiResponse<HighlightSelectResponse>> selectHighlight(
            @RequestBody HighlightSelectRequest request
    ) {
        Member member = SecurityUtils.getCurrentMember();

        return ResponseEntity.ok(ApiResponse.ok(highlightCommandService.selectHighlight(request, member)));
    }

    @PostMapping("/upload-result")
    public ResponseEntity<ApiResponse<?>> uploadHighlights(
            @RequestHeader("X-Member-Id") UUID memberId,
            @RequestBody HighlightRequest highlights
    ) {
        highlightCommandService.uploadHighlights(highlights,memberId);
        return ResponseEntity.ok(ApiResponse.okWithOutData());
    }
}
