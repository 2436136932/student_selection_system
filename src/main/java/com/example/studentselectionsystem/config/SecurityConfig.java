package com.example.studentselectionsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security配置类
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // 启用@PreAuthorize和@PostAuthorize注解
public class SecurityConfig {
    
    /**
     * CORS配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        
        return source;
    }
    
    /**
     * 安全过滤器链配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF防护（开发环境）
            .csrf(AbstractHttpConfigurer::disable)
            // 基本认证配置
            .authorizeHttpRequests(auth -> auth
                // 允许公开访问的接口
                .requestMatchers("/api/auth/**").permitAll()
                // 其他接口需要认证
                .anyRequest().authenticated()
            )
            // 添加CORS配置
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 启用表单登录（开发环境）
            .formLogin(AbstractHttpConfigurer::disable)
            // 启用HTTP基本认证
            .httpBasic(AbstractHttpConfigurer::disable)
            // 配置会话管理
            .sessionManagement(session -> session
                .maximumSessions(1)
                .expiredUrl("/api/auth/login")
            );
        
        return http.build();
    }

}

