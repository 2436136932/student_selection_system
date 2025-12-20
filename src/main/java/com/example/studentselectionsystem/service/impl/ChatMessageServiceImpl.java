package com.example.studentselectionsystem.service.impl;

import com.example.studentselectionsystem.entity.ChatMessage;
import com.example.studentselectionsystem.repository.ChatMessageRepository;
import com.example.studentselectionsystem.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 聊天消息服务实现类
 */
@Service
@Transactional
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Override
    public ChatMessage sendChatMessage(ChatMessage chatMessage) {
        // 设置创建时间
        chatMessage.setCreatedAt(new Date());
        chatMessage.setReadStatus("unread");
        chatMessageRepository.insert(chatMessage);
        return chatMessage;
    }

    @Override
    public List<ChatMessage> findChatMessagesBySessionId(Long sessionId) {
        return chatMessageRepository.selectBySessionId(sessionId);
    }

    @Override
    public List<ChatMessage> findChatMessagesBySessionIdAndSenderId(Long sessionId, Long senderId) {
        return chatMessageRepository.selectBySessionIdAndSenderId(sessionId, senderId);
    }

    @Override
    public void markMessageAsRead(Long messageId) {
        chatMessageRepository.updateReadStatusById(messageId);
    }

    @Override
    public void markAllMessagesAsRead(Long sessionId, Long receiverId) {
        chatMessageRepository.updateReadStatusBySessionId(sessionId, receiverId);
    }

    @Override
    public Optional<ChatMessage> findChatMessageById(Long id) {
        return Optional.ofNullable(chatMessageRepository.selectById(id));
    }

    @Override
    public List<ChatMessage> findAllChatMessages() {
        return chatMessageRepository.selectList(null);
    }
}