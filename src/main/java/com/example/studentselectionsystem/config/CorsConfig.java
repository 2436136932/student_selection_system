package com.example.studentselectionsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置类
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // 1.创建CorsConfiguration对象
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        
        // 2.设置允许跨域请求的源
        corsConfiguration.addAllowedOrigin("*");
        
        // 3.设置允许跨域请求的方法
        corsConfiguration.addAllowedMethod("*");
        
        // 4.设置允许跨域请求的头信息
        corsConfiguration.addAllowedHeader("*");
        
        // 5.设置是否允许发送Cookie
        corsConfiguration.setAllowCredentials(true);
        
        // 6.设置预检请求的缓存时间（单位：秒）
        corsConfiguration.setMaxAge(3600L);
        
        // 7.创建UrlBasedCorsConfigurationSource对象
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        
        // 8.注册CorsConfiguration对象
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        
        // 9.返回CorsFilter对象
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}