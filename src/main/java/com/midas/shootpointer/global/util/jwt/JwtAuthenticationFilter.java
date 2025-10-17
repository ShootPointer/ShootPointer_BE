package com.midas.shootpointer.global.util.jwt;

import com.midas.shootpointer.global.security.CustomUserDetailsService;
import com.midas.shootpointer.global.util.jwt.handler.JwtHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

        String token = jwtUtil.resolveToken(request);
        
        if (token != null && jwtHandler.validateToken(token)) {
            try {
                
                String email = jwtHandler.getEmailFromToken(token);

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

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
    
}
