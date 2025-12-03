package com.example.studentselectionsystem.model;

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
     * 评选年份
     */
    private String year;
    
    /**
     * 获奖描述
     */
    private String description;
    
    // Constructor with all arguments
    public Award(String awardId, String studentId, String awardName, String awardLevel, String year, String description) {
        this.awardId = awardId;
        this.studentId = studentId;
        this.awardName = awardName;
        this.awardLevel = awardLevel;
        this.year = year;
        this.description = description;
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
}