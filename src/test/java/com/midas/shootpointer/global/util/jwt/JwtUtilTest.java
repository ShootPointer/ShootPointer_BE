package com.midas.shootpointer.global.util.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    @Test
    void jwtUtilCreateAndParseTokenTest() {
        String secretKey = "this_is_a_very_long_test_secret_key_1234567890!";
        long expiration = 86400000L; // 24시간

        JwtUtil jwtUtil = new JwtUtil(secretKey);
        jwtUtil.ACCESS_EXP = expiration;

        String email = "test1234@naver.com";
        String nickname = "테스트";

        /*
        시나리오
        - 이메일, 닉네임으로 토큰 생성 후, 해당 토큰으로 이메일, 닉네임 추출한 결과가 실제 값이라 같은지 비교
         */
        String token = jwtUtil.createToken(email, nickname);

        String resultEmail = jwtUtil.getEmailFromToken(token);
        String resultNickname = jwtUtil.getNicknameFromToken(token);

        assertThat(resultEmail).isEqualTo(email);
        assertThat(resultNickname).isEqualTo(nickname);

        Claims claims = jwtUtil.parseToken(token);
        assertThat(jwtUtil.decodeBase64(claims.get("email", String.class))).isEqualTo(email);
    }

}