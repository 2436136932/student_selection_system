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
 * 竞赛实体类
 */
@Data
@TableName("competitions")
public class Competition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 竞赛ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 竞赛名称
     */
    @TableField("name")
    private String name;

    /**
     * 竞赛级别（国家级、省级、校级）
     */
    @TableField("level")
    private String level;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", updateStrategy = FieldStrategy.NEVER)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


}