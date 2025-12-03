package com.example.studentselectionsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 评奖标准实体类
 */
@TableName("courses") // 保持原有表名，避免数据库修改
public class Standard implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标准ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 标准名称
     */
    @TableField("name")
    private String name;

    /**
     * 权重
     */
    @TableField("credit") // 使用原有字段存储权重
    private BigDecimal weight;

    /**
     * 学期
     */
    @TableField("semester")
    private String semester;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", updateStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NEVER)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 评分关联（不映射到数据库）
     */
    @TableField(exist = false)
    private List<Score> scores;

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

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }
}