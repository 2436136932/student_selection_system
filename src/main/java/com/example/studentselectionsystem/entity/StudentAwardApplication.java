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

/**
 * 学生奖项申请表实体类
 */
@Data
@TableName("student_award_applications")
public class StudentAwardApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请ID
     */
    @TableId(type = IdType.AUTO, value = "application_id")
    private Long id;

    /**
     * 学生ID
     */
    @TableField("student_id")
    private Long studentId;

    /**
     * 学生信息（不映射到数据库）
     */
    @TableField(exist = false)
    private Student student;

    /**
     * 奖项ID
     */
    @TableField("award_id")
    private Long awardId;

    /**
     * 奖项信息（不映射到数据库）
     */
    @TableField(exist = false)
    private Award award;

    /**
     * 申请时间
     */
    @TableField("application_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applicationTime;

    /**
     * 申请理由
     */
    @TableField("description")
    private String description;

    /**
     * 状态：0-待审核, 1-教师审核通过，待管理员审批, 2-教师审核拒绝, 3-管理员审批通过, 4-管理员审批拒绝
     */
    @TableField("status")
    private Integer status;

    /**
     * 教师审批状态：0-待审核, 1-通过, 2-不通过
     */
    @TableField("teacher_approval_status")
    private Integer teacherApprovalStatus;

    /**
     * 教师审批时间
     */
    @TableField("teacher_approval_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date teacherApprovalTime;

    /**
     * 教师审批意见
     */
    @TableField("teacher_approval_comments")
    private String teacherApprovalComments;

    /**
     * 管理员审批状态：0-待审核, 1-通过, 2-不通过
     */
    @TableField("admin_approval_status")
    private Integer adminApprovalStatus;

    /**
     * 管理员审批时间
     */
    @TableField("admin_approval_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date adminApprovalTime;

    /**
     * 管理员审批意见
     */
    @TableField("admin_approval_comments")
    private String adminApprovalComments;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", updateStrategy = FieldStrategy.NEVER)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 申请材料文件路径
     */
    @TableField("material_path")
    private String materialPath;

    /**
     * 申请材料文件名
     */
    @TableField("material_name")
    private String materialName;

    /**
     * 申请材料文件大小（字节）
     */
    @TableField("material_size")
    private Long materialSize;

    /**
     * 申请材料文件类型
     */
    @TableField("material_type")
    private String materialType;
}