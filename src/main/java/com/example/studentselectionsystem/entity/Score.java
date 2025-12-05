package com.example.studentselectionsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩实体类
 */
@TableName("scores")
public class Score implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成绩ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID
     */
    @TableField("student_id")
    private Long studentId;

    /**
     * 学生对象（不映射到数据库）
     */
    @TableField(exist = false)
    @JsonIgnore
    private Student student;

    /**
     * 课程ID
     */
    @TableField("course_id")
    private Long courseId;

    /**
     * 课程对象（不映射到数据库）
     */
    @TableField(exist = false)
    @JsonIgnore
    private Course course;
    
    /**
     * 课程代码（不映射到数据库，用于接收前端参数）
     */
    @TableField(exist = false)
    private String courseCode;
    


    /**
     * 平时成绩
     */
    @TableField("usual_score")
    private BigDecimal usualScore;

    /**
     * 考试成绩
     */
    @TableField("exam_score")
    private BigDecimal examScore;

    /**
     * 总成绩
     */
    @TableField("total_score")
    private BigDecimal totalScore;

    /**
     * 等级（A/B/C/D/E/F）
     */
    @TableField("grade")
    private String grade;

    /**
     * 状态：1-已发布，0-未发布
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 课程名称（不映射到数据库）
     */
    @TableField(exist = false)
    private String courseName;
    
    /**
     * 学生姓名（不映射到数据库）
     */
    @TableField(exist = false)
    private String studentName;
    
    /**
     * 学期（不映射到数据库）
     */
    @TableField(exist = false)
    private String semester;
    
    /**
     * 学生学号（不映射到数据库，用于接收前端参数）
     */
    @TableField(exist = false)
    private String studentNumber;

    // Getter and Setter methods
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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public BigDecimal getUsualScore() {
        return usualScore;
    }

    public void setUsualScore(BigDecimal usualScore) {
        this.usualScore = usualScore;
    }

    public BigDecimal getExamScore() {
        return examScore;
    }

    public void setExamScore(BigDecimal examScore) {
        this.examScore = examScore;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }
}