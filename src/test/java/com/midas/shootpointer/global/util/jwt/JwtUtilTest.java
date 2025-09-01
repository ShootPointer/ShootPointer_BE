package com.midas.shootpointer.global.util.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    JwtUtil jwtUtil;

//    @Test
//    void jwtUtilCreateAndParseTokenTest() {
//        String secretKey = "this_is_a_very_long_test_secret_key_1234567890!";
//        long expiration = 86400000L; // 24시간
//
//        JwtUtil jwtUtil = new JwtUtil(secretKey);
//        jwtUtil.ACCESS_EXP = expiration;
//
//        String email = "test1234@naver.com";
//        String nickname = "테스트";
//
//        /*
//        시나리오
//        - 이메일, 닉네임으로 토큰 생성 후, 해당 토큰으로 이메일, 닉네임 추출한 결과가 실제 값이라 같은지 비교
//         */
//        String token = jwtUtil.createToken(email, nickname);
//
//        String resultEmail = jwtUtil.getEmailFromToken(token);
//        String resultNickname = jwtUtil.getNicknameFromToken(token);
//
//        assertThat(resultEmail).isEqualTo(email);
//        assertThat(resultNickname).isEqualTo(nickname);
//
//        Claims claims = jwtUtil.parseToken(token);
//        assertThat(jwtUtil.decodeBase64(claims.get("email", String.class))).isEqualTo(email);
//    }

    @Test
    void 액세스_리프레시토큰_테스트() {

        // given
        String email = "test1234@naver.com";
        String nickname = "홍길동";
        UUID adminId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        // when
        String accessToken = jwtUtil.createToken(adminId, email, nickname);
        String refreshToken = jwtUtil.createRefreshToken(email);

        // then
        assertThat(accessToken).isNotNull();
        assertThat(refreshToken).isNotNull();

        // JWT에서 email, nickname 값 추출
        String decodedEmail = jwtUtil.getEmailFromToken(accessToken);
        String decodedNickname = jwtUtil.getNicknameFromToken(accessToken);

        System.out.println("디코딩 이메일: " + decodedEmail);
        System.out.println("디코딩 닉네임: " + decodedNickname);
        System.out.println("accessToken: " + accessToken);
        System.out.println("refreshToken: " + refreshToken);
        //

    }


    @Test
    void testGetMemberIdFromToken() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiNjI5NDk3ZS0zYzVkLTQ0ZDEtYjJlNy1lZGMxNWIyY2FlYzUiLCJlbWFpbCI6ImRHdDJNREJBYm1GMlpYSXVZMjl0Iiwibmlja25hbWUiOiI2cm1BNjQrRTdKZXciLCJpYXQiOjE3NTA2OTQyMzksImV4cCI6MTc1MDc4MDYzOX0.gxxETNxzOapb2JoW4YH3J0-8bSPAYh04Np-bF7w-yZU";
        UUID memberId = jwtUtil.getMemberId(token);
        System.out.println("memberId = " + memberId);
    }



}