package com.midas.shootpointer.global.util.jwt;

import com.midas.shootpointer.global.annotation.CustomLog;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {

    private final Key key;

    @Value("${jwt.access_expiration_time}")
    private long ACCESS_EXP;

    @Value("${jwt.refresh_expiration_time}")
    private long REFRESH_EXP;

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }

    @CustomLog("== JWT 생성 ==")
    public String createToken(UUID memberId, String email, String nickname) {
        try {
            return Jwts.builder()
                    .setSubject(memberId.toString())
                    .claim("email", email)
                    .claim("nickname", nickname)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXP))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.JWT_CREATE_FAIL);
        }

    }

    @CustomLog("== JWT 파싱 ==")
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.JWT_PARSE_FAIL);
        }

    }

    @CustomLog("== 리프레시 토큰 생성 ==")
    public String createRefreshToken(String email) {
        try {
            return Jwts.builder()
                    .setSubject(UUID.randomUUID().toString())
                    .claim("email", email)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXP))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.JWT_CREATE_FAIL);
        }
    }
    
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");

        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    public String decodeBase64(String encoded) {
        return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
    }

    public String getEmailFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("email", String.class);
    }

    public String getNicknameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("nickname", String.class);
    }

    public UUID getMemberId(String token) {
        try {
            Claims claims = parseToken(token);
            String subject = claims.getSubject();
            return UUID.fromString(subject);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new CustomException(ErrorCode.JWT_MEMBER_ID_INVALID);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.JWT_PARSE_FAIL);
        }
    }

}
