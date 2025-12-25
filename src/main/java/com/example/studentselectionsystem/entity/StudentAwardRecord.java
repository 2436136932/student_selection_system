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
 * 学生获奖记录实体类
 */
@Data
@TableName("student_award_records")
public class StudentAwardRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 获奖记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 奖项ID
     */
    @TableField("award_id")
    private Long awardId;

    /**
     * 奖项信息（不映射到数据库）
     */
    @TableField(exist = false)
    private Award award;

    /**
     * 学生姓名
     */
    @TableField("student_name")
    private String studentName;

    /**
     * 学号
     */
    @TableField("student_number")
    private String studentNumber;

    /**
     * 班级
     */
    @TableField("class_name")
    private String className;

    /**
     * 专业
     */
    @TableField("major")
    private String major;

    /**
     * 奖项名称
     */
    @TableField("award_name")
    private String awardName;

    /**
     * 奖项级别（国家级、省级、校级）
     */
    @TableField("award_level")
    private String awardLevel;

    /**
     * 奖项类型（奖学金、优秀学生、优秀干部等）
     */
    @TableField("award_type")
    private String awardType;

    /**
     * 获奖时间
     */
    @TableField("award_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date awardTime;

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
    private Date updateTime;
}