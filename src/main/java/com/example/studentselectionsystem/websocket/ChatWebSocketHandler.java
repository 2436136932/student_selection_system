package com.example.studentselectionsystem.websocket;

import com.example.studentselectionsystem.entity.ChatMessage;
import com.example.studentselectionsystem.service.ChatMessageService;
import com.example.studentselectionsystem.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket聊天处理器
 */
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    // 存储用户会话映射，key为用户ID，value为WebSocketSession
    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // Jackson对象映射器，用于JSON序列化和反序列化
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 聊天消息服务，用于保存消息到数据库
    @Autowired
    private ChatMessageService chatMessageService;
    
    // JWT工具类，用于验证JWT令牌
    @Autowired
    private JwtUtil jwtUtil;
    
    // 用户详情服务，用于获取用户信息
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 当WebSocket连接建立时调用
     * @param session WebSocket会话
     * @throws Exception 异常
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从会话中获取用户ID和token，这里假设用户ID和token是通过URL参数传递的
        System.out.println("WebSocket连接建立，会话ID: " + session.getId());
        String userId = getUserIdFromSession(session);
        String token = getTokenFromSession(session);
        
        System.out.println("尝试建立WebSocket连接，userId: " + userId + ", token: " + (token != null ? "有token" : "无token"));
        
        if (userId != null && token != null) {
            try {
                // 验证token是否有效
                String username = jwtUtil.extractUsername(token);
                System.out.println("从token中提取的用户名: " + username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                if (jwtUtil.validateToken(token, userDetails)) {
                    sessions.put(userId, session);
                    System.out.println("用户 " + userId + " 连接成功，当前在线人数: " + sessions.size());
                    System.out.println("当前在线用户ID列表: " + sessions.keySet());
                } else {
                    System.out.println("用户 " + userId + " token无效，拒绝连接");
                    session.close();
                }
            } catch (Exception e) {
                System.out.println("用户 " + userId + " token验证失败，拒绝连接: " + e.getMessage());
                e.printStackTrace();
                session.close();
            }
        } else {
            System.out.println("用户ID或token缺失，拒绝连接");
            session.close();
        }
    }

    /**
     * 当WebSocket连接关闭时调用
     * @param session WebSocket会话
     * @param status 关闭状态
     * @throws Exception 异常
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            sessions.remove(userId);
            System.out.println("用户 " + userId + " 断开连接，当前在线人数: " + sessions.size());
        }
    }

    /**
     * 处理接收到的文本消息
     * @param session WebSocket会话
     * @param message 接收到的文本消息
     * @throws Exception 异常
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            // 解析接收到的JSON消息
            ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
            System.out.println("接收到消息: " + chatMessage.toString());

            // 保存消息到数据库
            chatMessageService.sendChatMessage(chatMessage);
            System.out.println("消息已保存到数据库: " + chatMessage.getId());
            
            // 将消息转发给目标用户
            sendMessageToUser(chatMessage.getReceiverId().toString(), message);
        } catch (Exception e) {
            System.err.println("处理消息失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 发送消息给特定用户
     * @param userId 用户ID
     * @param message 消息内容
     */
    public void sendMessageToUser(String userId, TextMessage message) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(message);
                System.out.println("发送消息给用户 " + userId + ": " + message.getPayload());
            } catch (IOException e) {
                System.err.println("发送消息失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送消息给所有在线用户
     * @param message 消息内容
     */
    public void sendMessageToAllUsers(TextMessage message) {
        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    System.err.println("发送消息失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从WebSocketSession中获取用户ID
     * @param session WebSocket会话
     * @return 用户ID
     */
    private String getUserIdFromSession(WebSocketSession session) {
        // 从URL参数中获取用户ID
        Map<String, Object> attributes = session.getAttributes();
        String userId = (String) attributes.get("userId");
        System.out.println("从attributes获取的userId: " + userId);
        
        if (userId == null) {
            // 尝试从请求参数中获取用户ID
            String queryString = session.getUri().getQuery();
            System.out.println("从URL获取到的queryString: " + queryString);
            
            if (queryString != null) {
                String[] params = queryString.split("&");
                for (String param : params) {
                    System.out.println("解析参数: " + param);
                    String[] keyValue = param.split("=", 2);
                    if (keyValue.length == 2) {
                        System.out.println("参数键: " + keyValue[0] + ", 值: " + keyValue[1]);
                        if ("userId".equals(keyValue[0])) {
                            userId = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                            System.out.println("解析到的userId: " + userId);
                            break;
                        }
                    }
                }
            }
        }
        return userId;
    }
    
    /**
     * 从WebSocketSession中获取token
     * @param session WebSocket会话
     * @return token
     */
    private String getTokenFromSession(WebSocketSession session) {
        // 尝试从请求参数中获取token
        String queryString = session.getUri().getQuery();
        System.out.println("从URL获取到的queryString: " + queryString);
        
        if (queryString != null) {
            String[] params = queryString.split("&");
            for (String param : params) {
                System.out.println("解析参数: " + param);
                String[] keyValue = param.split("=", 2);
                if (keyValue.length == 2) {
                    System.out.println("参数键: " + keyValue[0] + ", 值: " + keyValue[1]);
                    if ("token".equals(keyValue[0])) {
                        String token = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                        System.out.println("解析到的token: " + token.substring(0, Math.min(10, token.length())) + "...");
                        return token;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取当前在线用户数量
     * @return 在线用户数量
     */
    public int getOnlineUserCount() {
        System.out.println("获取在线用户数量，当前sessions大小: " + sessions.size());
        return sessions.size();
    }
    
    /**
     * 获取当前在线用户ID列表
     * @return 在线用户ID列表
     */
    public List<String> getOnlineUserIds() {
        System.out.println("获取在线用户ID列表，当前sessions大小: " + sessions.size());
        System.out.println("当前在线用户ID: " + sessions.keySet());
        return new ArrayList<>(sessions.keySet());
    }
}