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
    private Integer id;

    /**
     * 奖项名称
     */
    @TableField("name")
    private String name;

    /**
     * 奖项类型（奖学金、优秀学生、优秀干部等）
     */
    @TableField("type")
    private String type;

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
     * 创建时间
     */
    @TableField(value = "create_time", updateStrategy = FieldStrategy.NEVER)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

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
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}