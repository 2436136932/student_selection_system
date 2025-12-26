package com.example.studentselectionsystem.service.impl;

import com.example.studentselectionsystem.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件服务实现
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public boolean sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void sendAwardNotificationEmail(String studentName, String awardName, String awardLevel, String email) {
        String subject = "恭喜您获得" + awardName + "通知";
        String content = "尊敬的" + studentName + "同学：\n\n" +
                "恭喜您获得\"" + awardName + "\"（\"" + awardLevel + "\"）！\n\n" +
                "请您及时登录学生评奖评优系统查看详细获奖信息。\n\n" +
                "系统地址：http://localhost:5173/\n\n" +
                "此邮件为系统自动发送，请勿回复。\n\n" +
                "学生评奖评优系统\n" +
                new java.util.Date().toString();
        sendEmail(email, subject, content);
    }
}
