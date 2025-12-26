package com.example.studentselectionsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件发送服务接口
 */
public interface EmailService {

    /**
     * 发送简单文本邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 是否发送成功
     */
    boolean sendEmail(String to, String subject, String content);

    /**
     * 发送获奖通知邮件
     * @param studentName 学生姓名
     * @param awardName 奖项名称
     * @param awardLevel 奖项级别
     * @param email 学生邮箱
     */
    void sendAwardNotificationEmail(String studentName, String awardName, String awardLevel, String email);
}