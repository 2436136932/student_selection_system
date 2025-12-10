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
@TableName("student_award_applications")
public class StudentAwardApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请ID
     */
    @TableId(type = IdType.AUTO, value = "application_id")
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
     * 申请理由
     */
    @TableField("description")
    private String description;

    /**
     * 状态（0-待审核，1-通过，2-未通过）- 最终状态
     */
    @TableField("status")
    private Integer status;

    /**
     * 教师审批状态（0-待审核，1-通过，2-不通过）
     */
    @TableField("teacher_approval_status")
    private Integer teacherApprovalStatus;

    /**
     * 教师审批时间
     */
    @TableField("teacher_approval_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date teacherApprovalTime;

    /**
     * 教师审批意见
     */
    @TableField("teacher_approval_comments")
    private String teacherApprovalComments;

    /**
     * 管理员审批状态（0-待审核，1-通过，2-不通过）
     */
    @TableField("admin_approval_status")
    private Integer adminApprovalStatus;

    /**
     * 管理员审批时间
     */
    @TableField("admin_approval_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date adminApprovalTime;

    /**
     * 管理员审批意见
     */
    @TableField("admin_approval_comments")
    private String adminApprovalComments;

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

    public String getDescription() {
        return description;
    }

    public Integer getStatus() {
        return status;
    }

    public Integer getTeacherApprovalStatus() {
        return teacherApprovalStatus;
    }

    public Date getTeacherApprovalTime() {
        return teacherApprovalTime;
    }

    public String getTeacherApprovalComments() {
        return teacherApprovalComments;
    }

    public Integer getAdminApprovalStatus() {
        return adminApprovalStatus;
    }

    public Date getAdminApprovalTime() {
        return adminApprovalTime;
    }

    public String getAdminApprovalComments() {
        return adminApprovalComments;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setTeacherApprovalStatus(Integer teacherApprovalStatus) {
        this.teacherApprovalStatus = teacherApprovalStatus;
    }

    public void setTeacherApprovalTime(Date teacherApprovalTime) {
        this.teacherApprovalTime = teacherApprovalTime;
    }

    public void setTeacherApprovalComments(String teacherApprovalComments) {
        this.teacherApprovalComments = teacherApprovalComments;
    }

    public void setAdminApprovalStatus(Integer adminApprovalStatus) {
        this.adminApprovalStatus = adminApprovalStatus;
    }

    public void setAdminApprovalTime(Date adminApprovalTime) {
        this.adminApprovalTime = adminApprovalTime;
    }

    public void setAdminApprovalComments(String adminApprovalComments) {
        this.adminApprovalComments = adminApprovalComments;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}