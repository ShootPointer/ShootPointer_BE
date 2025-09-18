package com.midas.shootpointer;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.UUID;

/**
 * 테스트를 위한 Member와 CustomUserDetails 객체 생성 클래스
 */

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomMember> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomMember annotation) {

        //Member 객체 생성.
        Member member=Member.builder()
                .username(annotation.name())
                .email(annotation.email())
                .memberId(UUID.randomUUID())
                .build();

        SecurityContext context= SecurityContextHolder.createEmptyContext();
        CustomUserDetails userDetails=new CustomUserDetails(member);
        Authentication auth=UsernamePasswordAuthenticationToken.authenticated(userDetails,"password",userDetails.getAuthorities());

        context.setAuthentication(auth);
        return context;
    }
}
