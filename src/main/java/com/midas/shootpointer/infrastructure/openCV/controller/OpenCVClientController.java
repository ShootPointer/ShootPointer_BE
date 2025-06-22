package com.midas.shootpointer.infrastructure.openCV.controller;

import com.midas.shootpointer.global.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/video/upload")
public class OpenCVClientController {
    @Value("${opencv.proxy.upload-video}")
    private String uploadProxyUrl;
    @PostMapping
    public ResponseEntity<ApiResponse<String>> getUploadUrl(@RequestHeader("Authorization") String header){
        return ResponseEntity.ok(ApiResponse.ok(uploadProxyUrl));
    }
}
