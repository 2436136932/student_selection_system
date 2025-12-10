package com.example.studentselectionsystem.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评奖评优实体类
 */
public class Award {
    /**
     * 奖项ID
     */
    private String awardId;
    
    /**
     * 学生学号
     */
    private String studentId;
    
    /**
     * 奖项名称
     */
    private String awardName;
    
    /**
     * 奖项级别
     */
    private String awardLevel;
    
    /**
     * 奖项类型
     */
    private String awardType;
    
    /**
     * 评奖要求
     */
    private String requirement;
    
    /**
     * 名额
     */
    private Integer quota;
    
    /**
     * 开始时间
     */
    private Date startTime;
    
    /**
     * 评选年份
     */
    private String year;
    
    /**
     * 获奖描述
     */
    private String description;
    
    /**
     * 当前状态：待开始/进行中/已完成/已关闭
     */
    private String currentStatus;
    
    /**
     * 奖项状态：未发布/已发布/已结束
     */
    private String status;
    
    /**
     * 截止时间
     */
    private Date endTime;
    
    // Constructor with all arguments
    public Award(String awardId, String studentId, String awardName, String awardLevel, String year, String description) {
        this.awardId = awardId;
        this.studentId = studentId;
        this.awardName = awardName;
        this.awardLevel = awardLevel;
        this.year = year;
        this.description = description;
    }
    
    // Constructor with all fields including currentStatus and endTime
    public Award(String awardId, String studentId, String awardName, String awardLevel, String year, String description, String currentStatus, Date endTime) {
        this.awardId = awardId;
        this.studentId = studentId;
        this.awardName = awardName;
        this.awardLevel = awardLevel;
        this.year = year;
        this.description = description;
        this.currentStatus = currentStatus;
        this.endTime = endTime;
    }
    
    // No-argument constructor
    public Award() {
    }
    
    // Getter and Setter methods
    public String getAwardId() {
        return awardId;
    }
    
    public void setAwardId(String awardId) {
        this.awardId = awardId;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
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
    
    public String getYear() {
        return year;
    }
    
    public void setYear(String year) {
        this.year = year;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCurrentStatus() {
        return currentStatus;
    }
    
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
    
    public Date getEndTime() {
        return endTime;
    }
    
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}