package com.example.studentselectionsystem.config;

import com.example.studentselectionsystem.websocket.ChatWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket配置类
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private ChatWebSocketHandler chatWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 配置WebSocket处理路径，允许所有来源访问
        // 同时支持原生WebSocket和SockJS
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .setAllowedOrigins("*");
        
        // 添加SockJS支持，用于不支持WebSocket的浏览器
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .setAllowedOrigins("*")
                .withSockJS();
    }
}