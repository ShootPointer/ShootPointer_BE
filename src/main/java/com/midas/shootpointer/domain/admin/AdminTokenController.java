package com.midas.shootpointer.domain.admin;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/token")
public class AdminTokenController {

    private final AdminTokenProvider adminTokenProvider;
    private final AdminService adminService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<Map<String, String>> getAdminToken() {
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", adminTokenProvider.getAccessToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check")
    public ResponseEntity<Map<String, String>> syncMember(@RequestHeader("Authorization") String bearerToken) {
        String token = extractToken(bearerToken);
        Member member = adminService.testToken(token, jwtUtil);

        Map<String, String> response = new HashMap<>();
        response.put("status", "synced");
        response.put("memberId", member.getMemberId().toString());
        response.put("email", member.getEmail());
        response.put("nickname", member.getUsername());

        return ResponseEntity.ok(response);
    }

    private String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new IllegalArgumentException("Authorization 헤더가 잘못되었습니다.");
    }
}
