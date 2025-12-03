package com.example.studentselectionsystem.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 异议实体类
 */
@Data
@TableName("objections")
public class Objection implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 异议ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 学生ID
     */
    @TableField("student_id")
    private Long studentId;

    /**
     * 学生信息（不映射到数据库）
     */
    @TableField(exist = false)
    private Student student;

    /**
     * 评选结果ID
     */
    @TableField("result_id")
    private Integer resultId;

    /**
     * 评选结果信息（不映射到数据库）
     */
    @TableField(exist = false)
    private SelectionResult result;

    /**
     * 异议内容
     */
    @TableField("content")
    private String content;

    /**
     * 状态（0-待处理，1-已处理，2-已驳回）
     */
    @TableField("status")
    private Integer status;

    /**
     * 处理回复
     */
    @TableField("reply")
    private String reply;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", updateStrategy = FieldStrategy.NEVER)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}