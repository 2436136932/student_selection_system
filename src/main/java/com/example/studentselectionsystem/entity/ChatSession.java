package com.example.studentselectionsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天会话实体类
 */
@Data
@TableName("chat_sessions")
public class ChatSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户1 ID（发起方）
     */
    @TableField("user1_id")
    private Long user1Id;

    /**
     * 用户2 ID（接收方）
     */
    @TableField("user2_id")
    private Long user2Id;

    /**
     * 会话状态：active/closed
     */
    @TableField("status")
    private String status;

    /**
     * 最后一条消息内容
     */
    @TableField("last_message")
    private String lastMessage;

    /**
     * 最后一条消息时间
     */
    @TableField("last_message_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastMessageTime;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", updateStrategy = FieldStrategy.NEVER)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    // Getter methods
    public Long getId() {
        return id;
    }

    public Long getUser1Id() {
        return user1Id;
    }

    public Long getUser2Id() {
        return user2Id;
    }

    public String getStatus() {
        return status;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    // Setter methods
    public void setId(Long id) {
        this.id = id;
    }

    public void setUser1Id(Long user1Id) {
        this.user1Id = user1Id;
    }

    public void setUser2Id(Long user2Id) {
        this.user2Id = user2Id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setLastMessageTime(Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}