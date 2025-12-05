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


@SpringBootApplication
@ComponentScan(basePackages = "com.example.studentselectionsystem")
@MapperScan({"com.example.studentselectionsystem.mapper", "com.example.studentselectionsystem.repository"})
public class StudentAwardSelectionSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentAwardSelectionSystemApplication.class, args);
    }





}