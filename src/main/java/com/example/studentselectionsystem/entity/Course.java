package com.example.studentselectionsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 课程实体类
 */
@Data
@TableName("courses")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 课程ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程代码
     */
    @NotEmpty(message = "课程代码不能为空")
    @TableField("course_code")
    private String courseCode;

    /**
     * 课程名称
     */
    @NotEmpty(message = "课程名称不能为空")
    @TableField("course_name")
    private String courseName;

    /**
     * 学分
     */
    @NotNull(message = "学分不能为空")
    @TableField("credits")
    private BigDecimal credits;

    /**
     * 学时
     */
    @NotNull(message = "学时不能为空")
    @TableField("hours")
    private Integer hours;

    /**
     * 所属院系
     */
    @NotEmpty(message = "所属院系不能为空")
    @TableField("department")
    private String department;

    /**
     * 授课教师ID
     */
    @TableField("teacher_id")
    private Long teacherId;

    /**
     * 授课教师姓名（不映射到数据库）
     */
    @TableField(exist = false)
    private String teacherName;

    /**
     * 学期
     */
    @NotEmpty(message = "学期不能为空")
    @TableField("semester")
    private String semester;

    /**
     * 学年
     */
    @NotNull(message = "学年不能为空")
    @TableField("year")
    private Integer year;

    /**
     * 最大选课人数
     */
    @TableField("max_students")
    private Integer maxStudents;

    /**
     * 当前选课人数
     */
    @TableField("current_students")
    private Integer currentStudents;

    /**
     * 状态：1-开设，0-关闭
     */
    @TableField("status")
    private Integer status;

    /**
     * 课程描述
     */
    @TableField("description")
    private String description;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", updateStrategy = FieldStrategy.NEVER)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    /**
     * 课程成绩关联（不映射到数据库）
     */
    @TableField(exist = false)
    @JsonIgnore
    private List<Score> scores;

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public void setCredits(BigDecimal credits) {
        this.credits = credits;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }

    public Integer getCurrentStudents() {
        return currentStudents;
    }

    public void setCurrentStudents(Integer currentStudents) {
        this.currentStudents = currentStudents;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }
}