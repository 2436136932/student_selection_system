package com.example.studentselectionsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源配置类
 */
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源访问路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置/uploads/**路径映射到本地文件系统的G:/javacode/student-selection-system/uploads/目录
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:G:/javacode/student-selection-system/uploads/");
    }
}
