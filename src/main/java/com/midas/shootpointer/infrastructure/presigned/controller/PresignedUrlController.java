package com.midas.shootpointer.infrastructure.presigned.controller;

import com.midas.shootpointer.global.dto.ApiResponse;
import com.midas.shootpointer.global.security.SecurityUtils;
import com.midas.shootpointer.infrastructure.presigned.dto.PresignedUrlResponse;
import com.midas.shootpointer.infrastructure.presigned.service.PresignedUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PresignedUrlController {
    private final PresignedUrlService presignedUrlService;

    @GetMapping("/pre-signed")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> getPreSignedUrl(){
        UUID memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(ApiResponse.ok(presignedUrlService.createPresignedUrl(memberId)));
    }
}
