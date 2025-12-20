package com.example.studentselectionsystem.websocket;

import com.example.studentselectionsystem.entity.ChatMessage;
import com.example.studentselectionsystem.service.ChatMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
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

    /**
     * 当WebSocket连接建立时调用
     * @param session WebSocket会话
     * @throws Exception 异常
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从会话中获取用户ID，这里假设用户ID是通过URL参数传递的
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            sessions.put(userId, session);
            System.out.println("用户 " + userId + " 连接成功，当前在线人数: " + sessions.size());
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
        if (userId == null) {
            // 尝试从请求参数中获取用户ID
            String queryString = session.getUri().getQuery();
            if (queryString != null) {
                String[] params = queryString.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2 && "userId".equals(keyValue[0])) {
                        userId = keyValue[1];
                        break;
                    }
                }
            }
        }
        return userId;
    }

    /**
     * 获取当前在线用户数量
     * @return 在线用户数量
     */
    public int getOnlineUserCount() {
        return sessions.size();
    }
}