package com.midas.shootpointer.global.util.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private final Key key;
    @Value("${jwt.expiration_time}")
    protected long ACCESS_EXP;

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String email, String nickname) {
        String encodedEmail = Base64.getEncoder().encodeToString(email.getBytes(StandardCharsets.UTF_8));
        String encodedNickname = Base64.getEncoder().encodeToString(nickname.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(UUID.randomUUID().toString())
                .claim("email", encodedEmail)
                .claim("nickname", encodedNickname)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXP))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String decodeBase64(String encoded) {
        return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
    }

    public String getEmailFromToken(String token) {
        Claims claims = parseToken(token);
        String encodedEmail = claims.get("email", String.class);
        return decodeBase64(encodedEmail);
    }

    public String getNicknameFromToken(String token) {
        Claims claims = parseToken(token);
        String encodedNickname = claims.get("nickname", String.class);
        return decodeBase64(encodedNickname);
    }

}
