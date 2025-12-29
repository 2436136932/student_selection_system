package com.example.studentselectionsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天消息实体类
 */
@Data
@TableName("chat_messages")
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    @TableField("session_id")
    private Long sessionId;

    /**
     * 发送者ID
     */
    @TableField("sender_id")
    private Long senderId;

    /**
     * 接收者ID
     */
    @TableField("receiver_id")
    private Long receiverId;

    /**
     * 发送者类型：student/teacher/admin
     */
    @TableField("sender_type")
    private String senderType;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 阅读状态：read/unread
     */
    @TableField("read_status")
    private String readStatus;

    /**
     * 消息类型：text/image/emoji/file
     */
    @TableField("message_type")
    private String messageType = "text";

    /**
     * 文件名
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件URL
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 发送时间
     */
    @TableField(value = "created_at", updateStrategy = FieldStrategy.NEVER)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    // Getter methods
    public Long getId() {
        return id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public String getSenderType() {
        return senderType;
    }

    public String getContent() {
        return content;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    // Setter methods
    public void setId(Long id) {
        this.id = id;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}