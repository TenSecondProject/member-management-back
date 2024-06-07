package org.colcum.admin.global.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대하여
            .allowedOrigins("http://localhost:5173") // 모든 출처 허용
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // 허용할 HTTP 메소드 지정
            .allowedHeaders("*") // 모든 헤더 허용
            .allowCredentials(true)
            .maxAge(3600); // pre-flight 요청의 결과를 1시간 동안 캐시
    }

}
