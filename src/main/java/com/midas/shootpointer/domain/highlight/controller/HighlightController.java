package com.midas.shootpointer.domain.highlight.controller;

import com.midas.shootpointer.domain.highlight.dto.HighlightResponse;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectRequest;
import com.midas.shootpointer.domain.highlight.dto.HighlightSelectResponse;
import com.midas.shootpointer.domain.highlight.dto.UploadHighlight;
import com.midas.shootpointer.domain.highlight.service.command.HighlightCommandService;
import com.midas.shootpointer.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/highlight")
@RequiredArgsConstructor
public class HighlightController {
    private final HighlightCommandService highlightCommandService;

    @PostMapping("/select")
    public ResponseEntity<ApiResponse<HighlightSelectResponse>> selectHighlight(
            @RequestHeader("Authorization") String header,
            @RequestBody HighlightSelectRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(highlightCommandService.selectHighlight(request, header)));
    }

    @PostMapping("/upload-result")
    public ResponseEntity<ApiResponse<List<HighlightResponse>>> uploadHighlights(
            @RequestHeader("X-Member-Id") String memberId,
            @RequestHeader("Authorization") String token,
            @RequestPart(value = "uploadHighlightDto") UploadHighlight request,
            @RequestPart(value = "highlights") List<MultipartFile> highlights
    ) {
        return ResponseEntity.ok(ApiResponse.ok(highlightCommandService.uploadHighlights(memberId,token,request,highlights)));
    }
}
