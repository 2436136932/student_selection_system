package com.example.studentselectionsystem.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        // 创建BCrypt密码编码器
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        // 测试密码
        String password = "admin123";
        
        // 生成密码哈希
        String hashedPassword = passwordEncoder.encode(password);
        System.out.println("Generated hash for 'admin123': " + hashedPassword);
        
        // 验证提供的哈希是否匹配密码
        String providedHash = "$2a$10$7JB720yubVSZvUI0rEqK/.VqGO7oJkkrP9GtK20yW20KdbD5M1eT.";
        boolean matches = passwordEncoder.matches(password, providedHash);
        System.out.println("Does 'admin123' match the provided hash? " + matches);
    }
}