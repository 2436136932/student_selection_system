package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentselectionsystem.entity.VerificationCode;
import com.example.studentselectionsystem.mapper.VerificationCodeMapper;
import com.example.studentselectionsystem.service.EmailService;
import com.example.studentselectionsystem.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

/**
 * 验证码Service实现
 */
@Service
public class VerificationCodeServiceImpl extends ServiceImpl<VerificationCodeMapper, VerificationCode> implements VerificationCodeService {

    @Autowired
    private EmailService emailService;

    @Override
    public boolean sendVerificationCode(String email) {
        // 生成6位随机验证码
        String code = generateVerificationCode();
        // 设置过期时间为5分钟后
        Date expireTime = Date.from(LocalDateTime.now().plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant());
        
        // 创建验证码记录
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setCode(code);
        verificationCode.setExpireTime(expireTime);
        verificationCode.setCreatedAt(new Date());
        verificationCode.setStatus(0); // 0表示未使用
        
        // 保存到数据库
        boolean saved = this.save(verificationCode);
        if (!saved) {
            return false;
        }
        
        // 发送邮件
        String subject = "学生评奖评优系统 - 密码重置验证码";
        String content = "您的验证码是：" + code + "，有效期为5分钟，请尽快使用。";
        emailService.sendEmail(email, subject, content);
        return true;
    }

    @Override
    public boolean verifyCode(String email, String code) {
        // 查询未使用且未过期的验证码
        VerificationCode verificationCode = this.lambdaQuery()
                .eq(VerificationCode::getEmail, email)
                .eq(VerificationCode::getCode, code)
                .eq(VerificationCode::getStatus, 0)
                .gt(VerificationCode::getExpireTime, new Date())
                .one();
        
        return verificationCode != null;
    }

    @Override
    public boolean useCode(String email, String code) {
        // 验证验证码
        if (!verifyCode(email, code)) {
            return false;
        }
        
        // 更新验证码状态为已使用
        boolean updated = this.lambdaUpdate()
                .eq(VerificationCode::getEmail, email)
                .eq(VerificationCode::getCode, code)
                .eq(VerificationCode::getStatus, 0)
                .set(VerificationCode::getStatus, 1) // 1表示已使用
                .update();
        
        return updated;
    }

    /**
     * 生成6位随机验证码
     * @return 验证码
     */
    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    /**
     * 更新过期验证码状态为已过期
     */
    @Scheduled(cron = "0 */5 * * * ?") // 每5分钟执行一次
    public void updateExpiredCodes() {
        this.lambdaUpdate()
                .eq(VerificationCode::getStatus, 0) // 只处理未使用的验证码
                .lt(VerificationCode::getExpireTime, new Date()) // 过期时间小于当前时间
                .set(VerificationCode::getStatus, 2) // 更新为已过期状态
                .update();
    }
}
