package com.example.studentselectionsystem.service;

import com.example.studentselectionsystem.entity.ChatSession;

import java.util.List;
import java.util.Optional;

/**
 * 聊天会话服务接口
 */
public interface ChatSessionService {

    /**
     * 创建聊天会话
     * @param chatSession 聊天会话信息
     * @return 创建的聊天会话
     */
    ChatSession createChatSession(ChatSession chatSession);

    /**
     * 更新聊天会话
     * @param id 会话ID
     * @param chatSession 聊天会话信息
     * @return 更新后的聊天会话
     */
    ChatSession updateChatSession(Long id, ChatSession chatSession);

    /**
     * 关闭聊天会话
     * @param id 会话ID
     */
    void closeChatSession(Long id);

    /**
     * 根据ID查找聊天会话
     * @param id 会话ID
     * @return 聊天会话信息
     */
    Optional<ChatSession> findChatSessionById(Long id);

    /**
     * 根据用户ID查找聊天会话
     * @param userId 用户ID
     * @return 聊天会话列表
     */
    List<ChatSession> findChatSessionsByUserId(Long userId);

    /**
     * 获取所有聊天会话（管理员）
     * @return 聊天会话列表
     */
    List<ChatSession> findAllChatSessions();
    
    /**
     * 根据两个用户ID查找聊天会话
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 聊天会话信息
     */
    Optional<ChatSession> findChatSessionByUserIds(Long userId1, Long userId2);
}