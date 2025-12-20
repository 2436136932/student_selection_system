package com.example.studentselectionsystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentselectionsystem.entity.ChatSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * 聊天会话数据访问接口
 */
@Mapper
public interface ChatSessionRepository extends BaseMapper<ChatSession> {

    /**
     * 根据用户ID查找聊天会话
     * @param userId 用户ID
     * @return 聊天会话列表
     */
    @Select("SELECT * FROM chat_sessions WHERE user1_id = #{userId} OR user2_id = #{userId}")
    List<ChatSession> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据两个用户ID查找聊天会话
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 聊天会话信息
     */
    @Select("SELECT * FROM chat_sessions WHERE (user1_id = #{userId1} AND user2_id = #{userId2}) OR (user1_id = #{userId2} AND user2_id = #{userId1})")
    Optional<ChatSession> selectByUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}