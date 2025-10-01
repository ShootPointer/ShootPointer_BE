package com.midas.shootpointer.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
@Profile({"dev", "prod", "test"}) // ES 테스트에는 로딩 안 됨
public class JpaAuditingConfig {
}

