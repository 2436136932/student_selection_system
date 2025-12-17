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
import java.util.List;

/**
 * 奖项实体类
 */
@TableName("awards")
public class Award implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 奖项ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 奖项名称
     */
    @TableField("award_name")
    private String awardName;

    /**
     * 奖项级别（国家级、省级、校级等）
     */
    @TableField("award_level")
    private String awardLevel;

    /**
     * 奖项类型（奖学金、优秀学生、优秀干部等）
     */
    @TableField("award_type")
    private String awardType;

    /**
     * 评奖要求
     */
    @TableField("requirement")
    private String requirement;

    /**
     * 奖项描述
     */
    @TableField("description")
    private String description;

    /**
     * 名额
     */
    @TableField("quota")
    private Integer quota;

    /**
     * 开始时间
     */
    @TableField("start_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 创建时间
     */
    @TableField("created_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 奖项状态：未发布/已发布/已结束
     */
    @TableField("status")
    private String status;

    /**
     * 当前状态：待开始/进行中/已完成/已关闭
     */
    @TableField("current_status")
    private String currentStatus;

    /**
     * 当前阶段：学生申请/教师审批/管理员审批/结果公示
     */
    @TableField("current_stage")
    private String currentStage;

    /**
     * 审批教师ID
     */
    @TableField("approving_teacher_id")
    private Long approvingTeacherId;

    /**
     * 审批教师姓名
     */
    @TableField("approving_teacher_name")
    private String approvingTeacherName;

    /**
     * 奖项评选标准关联（不映射到数据库）
     */
    @TableField(exist = false)
    private List<SelectionCriteria> selectionCriteria;

    /**
     * 奖项评选流程关联（不映射到数据库）
     */
    @TableField(exist = false)
    private List<SelectionProcess> selectionProcesses;

    /**
     * 奖项申请关联（不映射到数据库）
     */
    @TableField(exist = false)
    private List<StudentAwardApplication> studentAwardApplications;

    /**
     * 奖项评选结果关联（不映射到数据库）
     */
    @TableField(exist = false)
    private List<SelectionResult> selectionResults;

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAwardName() {
        return awardName;
    }

    public void setAwardName(String awardName) {
        this.awardName = awardName;
    }

    public String getAwardLevel() {
        return awardLevel;
    }

    public void setAwardLevel(String awardLevel) {
        this.awardLevel = awardLevel;
    }

    public String getAwardType() {
        return awardType;
    }

    public void setAwardType(String awardType) {
        this.awardType = awardType;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuota() {
        return quota;
    }

    public void setQuota(Integer quota) {
        this.quota = quota;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public List<SelectionCriteria> getSelectionCriteria() {
        return selectionCriteria;
    }

    public void setSelectionCriteria(List<SelectionCriteria> selectionCriteria) {
        this.selectionCriteria = selectionCriteria;
    }

    public List<SelectionProcess> getSelectionProcesses() {
        return selectionProcesses;
    }

    public void setSelectionProcesses(List<SelectionProcess> selectionProcesses) {
        this.selectionProcesses = selectionProcesses;
    }

    public List<StudentAwardApplication> getStudentAwardApplications() {
        return studentAwardApplications;
    }

    public void setStudentAwardApplications(List<StudentAwardApplication> studentAwardApplications) {
        this.studentAwardApplications = studentAwardApplications;
    }

    public List<SelectionResult> getSelectionResults() {
        return selectionResults;
    }

    public void setSelectionResults(List<SelectionResult> selectionResults) {
        this.selectionResults = selectionResults;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public Long getApprovingTeacherId() {
        return approvingTeacherId;
    }

    public void setApprovingTeacherId(Long approvingTeacherId) {
        this.approvingTeacherId = approvingTeacherId;
    }

    public String getApprovingTeacherName() {
        return approvingTeacherName;
    }

    public void setApprovingTeacherName(String approvingTeacherName) {
        this.approvingTeacherName = approvingTeacherName;
    }
}