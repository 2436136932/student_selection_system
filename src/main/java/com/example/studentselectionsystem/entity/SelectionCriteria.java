package com.example.studentselectionsystem.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 评选标准实体类
 */
@Data
@TableName("selection_criteria")
public class SelectionCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标准ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 标准名称
     */
    @TableField("criterion_name")
    private String criterionName;

    /**
     * 标准类型（成绩、竞赛、科研、志愿等）
     */
    @TableField("criterion_type")
    private String criterionType;

    /**
     * 权重
     */
    @TableField("weight")
    private BigDecimal weight;

    /**
     * 阈值（最低要求）
     */
    @TableField("threshold")
    private BigDecimal threshold;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", updateStrategy = FieldStrategy.NEVER)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // Getter methods
    public Long getId() {
        return id;
    }

    public Integer getAwardId() {
        return awardId;
    }

    public Award getAward() {
        return award;
    }

    public String getCriterionName() {
        return criterionName;
    }

    public String getCriterionType() {
        return criterionType;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    public Date getCreateTime() {
        return createTime;
    }

    // Setter methods
    public void setId(Long id) {
        this.id = id;
    }

    public void setAwardId(Integer awardId) {
        this.awardId = awardId;
    }

    public void setAward(Award award) {
        this.award = award;
    }

    public void setCriterionName(String criterionName) {
        this.criterionName = criterionName;
    }

    public void setCriterionType(String criterionType) {
        this.criterionType = criterionType;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public void setThreshold(BigDecimal threshold) {
        this.threshold = threshold;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}