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
 * 学生竞赛关联实体类
 */
@Data
@TableName("student_competitions")
public class StudentCompetition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
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
     * 竞赛ID
     */
    @TableField("competition_id")
    private Integer competitionId;

    /**
     * 竞赛信息（不映射到数据库）
     */
    @TableField(exist = false)
    private Competition competition;

    /**
     * 获奖等级（一等奖、二等奖、三等奖、优秀奖）
     */
    @TableField("award_level")
    private String awardLevel;

    /**
     * 参赛时间
     */
    @TableField("competition_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date competitionTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", updateStrategy = FieldStrategy.NEVER)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}