package com.example.studentselectionsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类，用于配置静态资源访问
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源访问路径
     * 将/uploads目录映射为静态资源路径，允许前端直接访问上传的图片文件
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置/uploads目录为静态资源路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:G:/javacode/student-selection-system/uploads/");
    }

    /**
     * 配置CORS跨域请求
     * 允许前端应用访问后端API
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}