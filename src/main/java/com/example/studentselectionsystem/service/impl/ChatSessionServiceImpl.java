package com.example.studentselectionsystem.service.impl;

import com.example.studentselectionsystem.entity.ChatSession;
import com.example.studentselectionsystem.repository.ChatSessionRepository;
import com.example.studentselectionsystem.service.ChatSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 聊天会话服务实现类
 */
@Service
@Transactional
public class ChatSessionServiceImpl implements ChatSessionService {

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Override
    public ChatSession createChatSession(ChatSession chatSession) {
        // 设置创建时间和更新时间
        Date now = new Date();
        chatSession.setCreatedAt(now);
        chatSession.setUpdatedAt(now);
        chatSession.setStatus("active");
        chatSessionRepository.insert(chatSession);
        return chatSession;
    }

    @Override
    public ChatSession updateChatSession(Long id, ChatSession chatSession) {
        Optional<ChatSession> optionalChatSession = Optional.ofNullable(chatSessionRepository.selectById(id));
        if (optionalChatSession.isPresent()) {
            ChatSession existingChatSession = optionalChatSession.get();
            // 更新聊天会话信息
            existingChatSession.setLastMessage(chatSession.getLastMessage());
            existingChatSession.setLastMessageTime(chatSession.getLastMessageTime());
            existingChatSession.setUpdatedAt(new Date());
            chatSessionRepository.updateById(existingChatSession);
            return existingChatSession;
        }
        return null;
    }

    @Override
    public void closeChatSession(Long id) {
        Optional<ChatSession> optionalChatSession = Optional.ofNullable(chatSessionRepository.selectById(id));
        if (optionalChatSession.isPresent()) {
            ChatSession existingChatSession = optionalChatSession.get();
            existingChatSession.setStatus("closed");
            existingChatSession.setUpdatedAt(new Date());
            chatSessionRepository.updateById(existingChatSession);
        }
    }

    @Override
    public Optional<ChatSession> findChatSessionById(Long id) {
        return Optional.ofNullable(chatSessionRepository.selectById(id));
    }

    @Override
    public List<ChatSession> findChatSessionsByUserId(Long userId) {
        return chatSessionRepository.selectByUserId(userId);
    }

    @Override
    public List<ChatSession> findAllChatSessions() {
        return chatSessionRepository.selectList(null);
    }

    @Override
    public Optional<ChatSession> findChatSessionByUserIds(Long userId1, Long userId2) {
        return chatSessionRepository.selectByUserIds(userId1, userId2);
    }
}