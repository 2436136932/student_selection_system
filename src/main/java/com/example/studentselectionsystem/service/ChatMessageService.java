package com.example.studentselectionsystem.service;

import com.example.studentselectionsystem.entity.ChatMessage;

import java.util.List;
import java.util.Optional;

/**
 * 聊天消息服务接口
 */
public interface ChatMessageService {

    /**
     * 发送聊天消息
     * @param chatMessage 聊天消息信息
     * @return 发送的聊天消息
     */
    ChatMessage sendChatMessage(ChatMessage chatMessage);

    /**
     * 根据会话ID查找聊天消息
     * @param sessionId 会话ID
     * @return 聊天消息列表
     */
    List<ChatMessage> findChatMessagesBySessionId(Long sessionId);

    /**
     * 根据会话ID和发送者ID查找聊天消息
     * @param sessionId 会话ID
     * @param senderId 发送者ID
     * @return 聊天消息列表
     */
    List<ChatMessage> findChatMessagesBySessionIdAndSenderId(Long sessionId, Long senderId);

    /**
     * 标记消息为已读
     * @param messageId 消息ID
     */
    void markMessageAsRead(Long messageId);

    /**
     * 标记会话中所有消息为已读
     * @param sessionId 会话ID
     * @param receiverId 接收者ID
     */
    void markAllMessagesAsRead(Long sessionId, Long receiverId);

    /**
     * 根据ID查找聊天消息
     * @param id 消息ID
     * @return 聊天消息信息
     */
    Optional<ChatMessage> findChatMessageById(Long id);

    /**
     * 获取所有聊天消息（管理员）
     * @return 聊天消息列表
     */
    List<ChatMessage> findAllChatMessages();

    /**
     * 获取当前用户的未读消息总数
     * @param userId 用户ID
     * @return 未读消息总数
     */
    long countUnreadMessagesByUserId(Long userId);

    /**
     * 获取会话的未读消息数量
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 未读消息数量
     */
    long countUnreadMessagesBySessionIdAndUserId(Long sessionId, Long userId);
}