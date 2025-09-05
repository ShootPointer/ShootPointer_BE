package com.midas.shootpointer;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberQueryRepository;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HealthCheckController {
    private final JwtUtil jwtUtil;
    private final MemberQueryRepository memberQueryRepository;
    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck(){
        return ResponseEntity.ok("health-check");
    }

    @GetMapping("/test-member")
    public ResponseEntity<String> getJWT(){
        Member member=Member.builder()
                .email("test@naver.com")
                .username("test")
                .build();
        Member savedMember=memberQueryRepository.save(member);
        return ResponseEntity.ok(jwtUtil.createToken(savedMember.getMemberId(),member.getEmail(),member.getUsername()));
    }
}
