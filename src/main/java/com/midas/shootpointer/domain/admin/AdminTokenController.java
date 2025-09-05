package com.midas.shootpointer.domain.admin;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/token")
public class AdminTokenController {
    
    private final AdminTokenProvider tokenProvider;
    private final AdminService adminService;
    private final JwtUtil jwtUtil;
    
    // AccessToken, RefreshToken 발급
    @GetMapping
    public ResponseEntity<Map<String, String>> getTokens() {
        UUID adminId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        String email = "test@naver.com";
        String nickname = "홍길동";
        
        String accessToken = tokenProvider.createAccessToken(adminId, email, nickname);
        String refreshToken = tokenProvider.createAndSaveRefreshToken(email);
        
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        return ResponseEntity.ok(response);
    }
    
    // AccessToken으로 Member 확ㄴ인
    @PostMapping("/check")
    public ResponseEntity<Map<String, String>> syncMember(@RequestHeader("Authorization") String bearerToken) {
        String token = extractToken(bearerToken);
        String email = jwtUtil.getEmailFromToken(token);
        String nickname = jwtUtil.getNicknameFromToken(token);
        
        Member member = adminService.syncMember(email, nickname);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "synced");
        response.put("memberId", member.getMemberId().toString());
        response.put("email", member.getEmail());
        response.put("nickname", member.getUsername());
        return ResponseEntity.ok(response);
    }
    
    // RefreshToken 조회
    @GetMapping("/refresh/{email}")
    public ResponseEntity<Map<String, String>> getRefreshToken(@PathVariable String email) {
        Map<String, String> response = new HashMap<>();
        response.put("refreshToken", tokenProvider.getRefreshToken(email).orElse("없음"));
        return ResponseEntity.ok(response);
    }
    
    private String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new IllegalArgumentException("Authorization 헤더가 잘못되었습니다.");
    }
}
