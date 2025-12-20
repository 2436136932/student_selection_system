package com.example.studentselectionsystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentselectionsystem.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

/**
 * 聊天消息数据访问接口
 */
@Mapper
public interface ChatMessageRepository extends BaseMapper<ChatMessage> {

    /**
     * 根据会话ID查找聊天消息
     * @param sessionId 会话ID
     * @return 聊天消息列表
     */
    @Select("SELECT * FROM chat_messages WHERE session_id = #{sessionId} ORDER BY created_at ASC")
    List<ChatMessage> selectBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 根据会话ID和发送者ID查找聊天消息
     * @param sessionId 会话ID
     * @param senderId 发送者ID
     * @return 聊天消息列表
     */
    @Select("SELECT * FROM chat_messages WHERE session_id = #{sessionId} AND sender_id = #{senderId} ORDER BY created_at ASC")
    List<ChatMessage> selectBySessionIdAndSenderId(@Param("sessionId") Long sessionId, @Param("senderId") Long senderId);

    /**
     * 标记消息为已读
     * @param messageId 消息ID
     */
    @Update("UPDATE chat_messages SET read_status = 'read' WHERE id = #{messageId}")
    void updateReadStatusById(@Param("messageId") Long messageId);

    /**
     * 标记会话中所有消息为已读
     * @param sessionId 会话ID
     * @param receiverId 接收者ID
     */
    @Update("UPDATE chat_messages SET read_status = 'read' WHERE session_id = #{sessionId} AND sender_id != #{receiverId}")
    void updateReadStatusBySessionId(@Param("sessionId") Long sessionId, @Param("receiverId") Long receiverId);

    /**
     * 获取会话中未读消息数量
     * @param sessionId 会话ID
     * @param receiverId 接收者ID
     * @return 未读消息数量
     */
    @Select("SELECT COUNT(*) FROM chat_messages WHERE session_id = #{sessionId} AND sender_id != #{receiverId} AND read_status = 'unread'")
    long countUnreadMessages(@Param("sessionId") Long sessionId, @Param("receiverId") Long receiverId);
}