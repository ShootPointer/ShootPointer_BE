package com.midas.shootpointer.domain.highlight.controller;

import com.midas.shootpointer.domain.highlight.business.HighlightManager;
import com.midas.shootpointer.domain.highlight.dto.HighlightResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.highlight.dto.UploadHighlight;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.dto.ApiResponse;
import com.midas.shootpointer.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/highlight")
@RequiredArgsConstructor
public class HighlightCommandController {
    private final HighlightManager manager;

    @PostMapping("/select")
    public ResponseEntity<ApiResponse<HighlightSelectResponse>> selectHighlight(
            @RequestBody HighlightSelectRequest request
    ) {
        Member member = SecurityUtils.getCurrentMember();

        return ResponseEntity.ok(ApiResponse.ok(manager.selectHighlight(request, member)));
    }

    @PostMapping("/upload-result")
    public ResponseEntity<ApiResponse<List<HighlightResponse>>> uploadHighlights(
            @RequestHeader("X-Member-Id") UUID memberId,
            @RequestPart(value = "uploadHighlightDto") UploadHighlight request,
            @RequestPart(value = "highlights") List<MultipartFile> highlights
    ) {
        return ResponseEntity.ok(ApiResponse.ok(manager.uploadHighlights(request,highlights,memberId)));
    }
}
