package com.midas.shootpointer.domain.admin;

import com.midas.shootpointer.global.util.jwt.JwtUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdminTokenProvider {

    private final JwtUtil jwtUtil;

    @Getter
    private String accessToken;

    @PostConstruct
    public void init() {
        UUID adminId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        String email = "test@naver.com";
        String nickname = "홍길동";

        this.accessToken = jwtUtil.createToken(adminId, email, nickname);
    }

}
