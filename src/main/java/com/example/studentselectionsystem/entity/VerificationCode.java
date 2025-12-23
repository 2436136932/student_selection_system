package com.example.studentselectionsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 验证码实体类
 */
@TableName("verification_codes")
@Data
public class VerificationCode implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 验证码ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 验证码
     */
    @TableField("code")
    private String code;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    /**
     * 创建时间
     */
    @TableField("created_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /**
     * 状态：0-未使用，1-已使用，2-已过期
     */
    @TableField("status")
    private Integer status;
}
