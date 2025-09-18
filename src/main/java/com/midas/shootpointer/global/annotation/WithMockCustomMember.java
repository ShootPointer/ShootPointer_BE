package com.midas.shootpointer.global.annotation;

import com.midas.shootpointer.global.aop.WithMockCustomUserSecurityContextFactory;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomMember {
    String email() default "test@naver.com";
    String name() default "test";
}
