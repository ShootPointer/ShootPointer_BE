package com.midas.shootpointer.global.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo(){
        return new Info()
                .title("슛포인터 Swagger")
                .description("REST API")
                .description("""
                                 **ShootPointer WebSocket API 설명**
                                
                                WebSocket은 Swagger에서 직접 테스트할 수 없습니다.
                                하지만 아래 엔드포인트를 통해 실시간 상태를 구독할 수 있습니다:
                                
                                `ws://localhost:9000/ws/progress?token={JWT}`
                                """)
                .version("1.0.0");
    }
}
