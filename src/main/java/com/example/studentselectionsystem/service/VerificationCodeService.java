package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentselectionsystem.entity.VerificationCode;

/**
 * 验证码Service
 */
public interface VerificationCodeService extends IService<VerificationCode> {
    /**
     * 生成并发送验证码
     * @param email 邮箱
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String email);
    
    /**
     * 验证验证码
     * @param email 邮箱
     * @param code 验证码
     * @return 是否验证成功
     */
    boolean verifyCode(String email, String code);
    
    /**
     * 使用验证码（验证后标记为已使用）
     * @param email 邮箱
     * @param code 验证码
     * @return 是否使用成功
     */
    boolean useCode(String email, String code);
}
