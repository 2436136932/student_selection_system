package com.example.studentselectionsystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器 - 用于测试API是否可以正常访问
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Pong!");
        response.put("timestamp", System.currentTimeMillis());
        System.out.println("TestController - ping() 方法被调用");
        return response;
    }

    @PostMapping("/echo")
    public Map<String, Object> echo(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Received: " + message);
        response.put("timestamp", System.currentTimeMillis());
        System.out.println("TestController - echo() 方法被调用，收到消息: " + message);
        return response;
    }
}