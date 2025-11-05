package com.midas.shootpointer.global.util.jwt;

import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import com.midas.shootpointer.global.security.CustomUserDetailsService;
import com.midas.shootpointer.global.util.jwt.handler.JwtHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtHandler jwtHandler;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    
    @Value("${encrypt.key}")
    private String encryptionKey;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

        String token = jwtUtil.resolveToken(request);
        
        if (token != null && jwtHandler.validateToken(token)) {
            try {
                String email = jwtHandler.getEmailFromToken(token); // 평문 이메일을
                String encryptedEmail = encryptEmail(email); // 암호화 해서 DB 조회

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(encryptedEmail);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                );
                
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            } catch (Exception e) {
                // 인증 실패 시 SecurityContext 초기화
                SecurityContextHolder.clearContext();
            }
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("\uD83D\uDD10 Filter Authentication: {}", auth);

        filterChain.doFilter(request, response);
    }
    
    /**
     * 이메일 암호화
     * @param plainEmail
     * @return
     */
    private String encryptEmail(String plainEmail) {
        try {
            byte[] keyBytes = hexStringToByteArray(encryptionKey);
            // AES-256 암호화
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] encrypted = cipher.doFinal(plainEmail.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
            
        } catch (Exception e) {
            log.error("이메일 암호화 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    private byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
    
}
