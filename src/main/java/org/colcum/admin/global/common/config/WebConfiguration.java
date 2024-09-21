package org.colcum.admin.global.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Value("${cors.origin}")
    private String corsOrigin;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(corsOrigin)
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*") // 모든 헤더 허용
            .allowCredentials(true)
            .maxAge(3600);
    }

}
