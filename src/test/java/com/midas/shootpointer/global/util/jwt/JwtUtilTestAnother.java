package com.midas.shootpointer.global.util.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtUtilTestAnother {

    private JwtUtil jwtUtil;

    private final String rawSecret = "4qmZKqt8Kwd2MCbGj39akXECPZr9hPUlgxYq2L6e9ho=";
    private final long accessExp = 1000 * 60 * 60 * 24;
    private final long refreshExp = 1000 * 60 * 60 * 72;

    private final UUID testMemberId = UUID.randomUUID();
    private final String testEmail = "tester@example.com";
    private final String testNickname = "홍길동";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(rawSecret);
        try {
            java.lang.reflect.Field accessExpField = JwtUtil.class.getDeclaredField("ACCESS_EXP");
            java.lang.reflect.Field refreshExpField = JwtUtil.class.getDeclaredField("REFRESH_EXP");
            accessExpField.setAccessible(true);
            refreshExpField.setAccessible(true);
            accessExpField.set(jwtUtil, accessExp);
            refreshExpField.set(jwtUtil, refreshExp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("✅ AccessToken 생성 및 파싱 성공")
    void createAndParseAccessToken() {
        String token = jwtUtil.createToken(testMemberId, testEmail, testNickname);
        System.out.println("✅ AccessToken = " + token);

        Claims claims = jwtUtil.parseToken(token);
        String encodedEmail = claims.get("email", String.class);
        String encodedNickname = claims.get("nickname", String.class);
        String subject = claims.getSubject();

        System.out.println("🔓 subject (memberId) = " + subject);
        System.out.println("🔒 encodedEmail = " + encodedEmail);
        System.out.println("🔒 encodedNickname = " + encodedNickname);


        assertThat(subject).isEqualTo(testMemberId.toString());
        assertThat(encodedNickname).isEqualTo(testNickname);
    }

    @Test
    @DisplayName("✅ getMemberId, getEmailFromToken, getNicknameFromToken 정상 작동")
    void testGettersFromToken() {
        String token = jwtUtil.createToken(testMemberId, testEmail, testNickname);
        System.out.println("✅ AccessToken = " + token);

        UUID extractedId = jwtUtil.getMemberId(token);
        String extractedEmail = jwtUtil.getEmailFromToken(token);
        String extractedNickname = jwtUtil.getNicknameFromToken(token);

        System.out.println("🔓 Extracted MemberId = " + extractedId);
        System.out.println("🔓 Extracted Email = " + extractedEmail);
        System.out.println("🔓 Extracted Nickname = " + extractedNickname);

        assertThat(extractedId).isEqualTo(testMemberId);
        assertThat(extractedEmail).isEqualTo(testEmail);
        assertThat(extractedNickname).isEqualTo(testNickname);
    }

    @Test
    @DisplayName("✅ RefreshToken 생성 및 이메일 디코딩 성공")
    void createRefreshToken() {
        String refreshToken = jwtUtil.createRefreshToken(testEmail);
        System.out.println("✅ RefreshToken = " + refreshToken);

        Claims claims = jwtUtil.parseToken(refreshToken);
        String encodedEmail = claims.get("email", String.class);


        System.out.println("🔒 encodedEmail = " + encodedEmail);
        System.out.println("🔓 subject (random UUID) = " + claims.getSubject());

        assertThat(claims.getSubject()).isNotBlank();
    }
}
