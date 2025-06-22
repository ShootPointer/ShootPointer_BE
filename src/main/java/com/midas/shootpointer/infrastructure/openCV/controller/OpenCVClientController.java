package com.midas.shootpointer.infrastructure.openCV.controller;

import com.midas.shootpointer.domain.member.repository.MemberRepository;
import com.midas.shootpointer.global.dto.ApiResponse;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
import com.midas.shootpointer.infrastructure.openCV.OpenCVProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/video/upload")
public class OpenCVClientController {
    private final String openCVProxyUrl;
    private final String openCVUrl;
    private final JwtUtil jwtUtil;
    public  OpenCVClientController(OpenCVProperties openCVProperties,JwtUtil jwtUtil){
        this.openCVUrl=openCVProperties.getUrl();
        this.openCVProxyUrl=openCVProperties.getApi().getProxy().getUploadVideo();
        this.jwtUtil=jwtUtil;
    }
    @GetMapping
    public ResponseEntity<ApiResponse<String>> getUploadUrl(@RequestHeader("Authorization") String header){
        UUID memberId=jwtUtil.getUserId();
        return ResponseEntity.ok(ApiResponse.ok(openCVUrl+"/"+openCVProxyUrl+"?memberId="+memberId));
    }
}
