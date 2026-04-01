package com.example.studentselectionsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局CORS配置类
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许所有/api/**路径的请求跨域访问
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173","http://localhost:5174","http://10.11.250.206:5173","http://10.11.250.206:5174")  // 允许前端域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的HTTP方法
                .allowedHeaders("*")  // 允许所有请求头
                .allowCredentials(true)  // 允许携带凭证
                .maxAge(3600);  // 预检请求的有效期，单位秒
                
        // 允许WebSocket路径跨域访问
        registry.addMapping("/ws/**")
                .allowedOrigins("http://localhost:5173","http://localhost:5174","http://10.11.250.206:5173","http://10.11.250.206:5174")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
