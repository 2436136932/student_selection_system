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

/**
 * 评选标准实体类
 */
@Data
@TableName("selection_criteria")
public class SelectionCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标准ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 奖项ID
     */
    @TableField("award_id")
    private Integer awardId;

    /**
     * 奖项信息（不映射到数据库）
     */
    @TableField(exist = false)
    private Award award;

    /**
     * 标准名称
     */
    @TableField("criterion_name")
    private String criterionName;

    /**
     * 标准类型（成绩、竞赛、科研、志愿等）
     */
    @TableField("criterion_type")
    private String criterionType;

    /**
     * 权重
     */
    @TableField("weight")
    private BigDecimal weight;

    /**
     * 阈值（最低要求）
     */
    @TableField("threshold")
    private BigDecimal threshold;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", updateStrategy = FieldStrategy.NEVER)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}