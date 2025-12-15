package com.example.studentselectionsystem;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@ComponentScan(basePackages = "com.example.studentselectionsystem")
@MapperScan({"com.example.studentselectionsystem.mapper", "com.example.studentselectionsystem.repository"})
@EnableScheduling
public class StudentAwardSelectionSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentAwardSelectionSystemApplication.class, args);
    }

    /**
     * MyBatis Plus分页插件配置
     */
    @Bean
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        // 设置数据库类型为MySQL
        paginationInterceptor.setDbType(DbType.MYSQL);
        // 设置请求的页面大于最大页后操作，true调回到首页，false继续请求。默认false
        paginationInterceptor.setOverflow(true);
        // 设置单页最大记录数
        paginationInterceptor.setMaxLimit(500L);
        return paginationInterceptor;
    }





}