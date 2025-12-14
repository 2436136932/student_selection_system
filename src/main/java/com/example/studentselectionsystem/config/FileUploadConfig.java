package com.example.studentselectionsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

/**
 * 文件上传配置类
 */
@Configuration
public class FileUploadConfig {

    /**
     * 配置文件上传解析器
     * Spring Boot 3.x 推荐使用 StandardServletMultipartResolver
     * 基于 Servlet 3.0+ 标准实现，无需额外依赖
     */
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
