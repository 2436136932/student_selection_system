package com.example.studentselectionsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("recommendation_weights")
public class RecommendationWeight {

    @TableId(type = IdType.AUTO)
    private Long id;

    private BigDecimal gradeWeight;

    private BigDecimal awardWeight;

    private BigDecimal majorWeight;

    private BigDecimal historyWeight;

    private BigDecimal competitionWeight;

    private LocalDateTime updatedAt;

    private Long updatedBy;
}
