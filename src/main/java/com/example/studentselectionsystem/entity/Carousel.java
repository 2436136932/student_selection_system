package com.example.studentselectionsystem.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("carousel")
public class Carousel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String imageUrl;
    private String title;
    private String description;
    private Integer sortOrder;
    private Integer status;
    private Date createdAt;
    private Date updatedAt;
}
