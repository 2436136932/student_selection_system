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
 * 学生奖项申请表实体类
 */
@Data
@TableName("student_award_application")
public class StudentAwardApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请ID
     */
    @TableId(type = IdType.AUTO)
    @TableField("application_id")
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
     * 申请时间
     */
    @TableField("application_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applicationTime;

    /**
     * 状态（0-待审核，1-通过，2-未通过）
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", updateStrategy = FieldStrategy.NEVER)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    // Getter methods
    public Integer getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Student getStudent() {
        return student;
    }

    public Integer getAwardId() {
        return awardId;
    }

    public Award getAward() {
        return award;
    }

    public Date getApplicationTime() {
        return applicationTime;
    }

    public Integer getStatus() {
        return status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    // Setter methods
    public void setId(Integer id) {
        this.id = id;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setAwardId(Integer awardId) {
        this.awardId = awardId;
    }

    public void setAward(Award award) {
        this.award = award;
    }

    public void setApplicationTime(Date applicationTime) {
        this.applicationTime = applicationTime;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}