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
     * 状态：0-待审核, 1-教师审核通过，待管理员审批, 2-教师审核拒绝, 3-管理员审批通过, 4-管理员审批拒绝
     */
    @TableField("status")
    private Integer status;

    /**
     * 教师审批状态：0-待审核, 1-通过, 2-不通过
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
     * 管理员审批状态：0-待审核, 1-通过, 2-不通过
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

    /**
     * 申请材料文件路径
     */
    @TableField("material_path")
    private String materialPath;

    /**
     * 申请材料文件名
     */
    @TableField("material_name")
    private String materialName;

    /**
     * 申请材料文件大小（字节）
     */
    @TableField("material_size")
    private Long materialSize;

    /**
     * 申请材料文件类型
     */
    @TableField("material_type")
    private String materialType;

    // 手动添加getter/setter方法，确保编译通过
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Long getAwardId() {
        return awardId;
    }

    public void setAwardId(Long awardId) {
        this.awardId = awardId;
    }

    public Award getAward() {
        return award;
    }

    public void setAward(Award award) {
        this.award = award;
    }

    public Date getApplicationTime() {
        return applicationTime;
    }

    public void setApplicationTime(Date applicationTime) {
        this.applicationTime = applicationTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTeacherApprovalStatus() {
        return teacherApprovalStatus;
    }

    public void setTeacherApprovalStatus(Integer teacherApprovalStatus) {
        this.teacherApprovalStatus = teacherApprovalStatus;
    }

    public Date getTeacherApprovalTime() {
        return teacherApprovalTime;
    }

    public void setTeacherApprovalTime(Date teacherApprovalTime) {
        this.teacherApprovalTime = teacherApprovalTime;
    }

    public String getTeacherApprovalComments() {
        return teacherApprovalComments;
    }

    public void setTeacherApprovalComments(String teacherApprovalComments) {
        this.teacherApprovalComments = teacherApprovalComments;
    }

    public Integer getAdminApprovalStatus() {
        return adminApprovalStatus;
    }

    public void setAdminApprovalStatus(Integer adminApprovalStatus) {
        this.adminApprovalStatus = adminApprovalStatus;
    }

    public Date getAdminApprovalTime() {
        return adminApprovalTime;
    }

    public void setAdminApprovalTime(Date adminApprovalTime) {
        this.adminApprovalTime = adminApprovalTime;
    }

    public String getAdminApprovalComments() {
        return adminApprovalComments;
    }

    public void setAdminApprovalComments(String adminApprovalComments) {
        this.adminApprovalComments = adminApprovalComments;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getMaterialPath() {
        return materialPath;
    }

    public void setMaterialPath(String materialPath) {
        this.materialPath = materialPath;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Long getMaterialSize() {
        return materialSize;
    }

    public void setMaterialSize(Long materialSize) {
        this.materialSize = materialSize;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }
}