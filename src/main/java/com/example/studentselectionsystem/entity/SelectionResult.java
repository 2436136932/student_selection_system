package com.example.studentselectionsystem.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 评选结果实体类
 */
@Data
@TableName("selection_result")
public class SelectionResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 结果ID
     */
    @TableId(type = IdType.AUTO, value = "result_id")
    private Long id;

    /**
     * 申请ID
     */
    @TableField("application_id")
    private Long applicationId;

    /**
     * 申请信息（不映射到数据库）
     */
    @TableField(exist = false)
    private StudentAwardApplication application;

    /**
     * 评分
     */
    @TableField("score")
    private BigDecimal score;

    /**
     * 评语
     */
    @TableField("comment")
    private String comment;

    /**
     * 评审人
     */
    @TableField("reviewer")
    private String reviewer;

    /**
     * 评审时间
     */
    @TableField("review_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewDate;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", updateStrategy = FieldStrategy.NEVER)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

}