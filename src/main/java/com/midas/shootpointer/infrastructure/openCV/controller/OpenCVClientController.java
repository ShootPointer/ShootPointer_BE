package com.midas.shootpointer.infrastructure.openCV.controller;

import com.midas.shootpointer.global.dto.ApiResponse;
import com.midas.shootpointer.infrastructure.openCV.OpenCVProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/video/upload")
public class OpenCVClientController {
    private final String openCVProxyUrl;
    private final String openCVUrl;
    public  OpenCVClientController(OpenCVProperties openCVProperties){
        this.openCVUrl=openCVProperties.getUrl();
        this.openCVProxyUrl=openCVProperties.getApi().getProxy().getUploadVideo();
    }
    @GetMapping
    public ResponseEntity<ApiResponse<String>> getUploadUrl(@RequestHeader("Authorization") String header){
        return ResponseEntity.ok(ApiResponse.ok(openCVUrl+"/"+openCVProxyUrl));
    }
}
