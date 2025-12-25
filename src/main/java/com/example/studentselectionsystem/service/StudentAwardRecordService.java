package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentselectionsystem.entity.StudentAwardRecord;

import java.util.List;
import java.util.Map;

/**
 * 学生获奖记录服务接口
 */
public interface StudentAwardRecordService {

    /**
     * 创建获奖记录
     * @param record 获奖记录信息
     * @return 创建的获奖记录
     */
    StudentAwardRecord createRecord(StudentAwardRecord record);

    /**
     * 根据ID查找获奖记录
     * @param id 获奖记录ID
     * @return 获奖记录信息
     */
    StudentAwardRecord findRecordById(Long id);

    /**
     * 根据学生ID查找获奖记录
     * @param studentId 学生ID
     * @return 获奖记录列表
     */
    List<StudentAwardRecord> findRecordsByStudentId(Long studentId);

    /**
     * 分页查找所有获奖记录
     * @param page 分页参数
     * @return 获奖记录分页结果
     */
    IPage<StudentAwardRecord> findAllRecords(Page<StudentAwardRecord> page);

    /**
     * 根据条件分页查找获奖记录
     * @param page 分页参数
     * @param studentId 学生ID（可选）
     * @param awardId 奖项ID（可选）
     * @param awardLevel 奖项级别（可选）
     * @param awardType 奖项类型（可选）
     * @param studentName 学生姓名（可选）
     * @param awardName 奖项名称（可选）
     * @param startTime 开始时间（可选，格式：YYYY-MM-DD）
     * @param endTime 结束时间（可选，格式：YYYY-MM-DD）
     * @param studentNumber 学号（可选）
     * @param className 班级（可选）
     * @param major 专业（可选）
     * @return 获奖记录分页结果
     */
    IPage<StudentAwardRecord> findRecordsByCondition(Page<StudentAwardRecord> page, Long studentId, Long awardId, 
                                                    String awardLevel, String awardType, String studentName, String awardName, 
                                                    String startTime, String endTime, String studentNumber, String className, String major);

    /**
     * 根据条件查找获奖记录（用于导出Excel）
     * @param params 查询参数
     * @return 获奖记录列表
     */
    List<StudentAwardRecord> findRecordsForExport(Map<String, Object> params);

    /**
     * 根据学生ID删除获奖记录
     * @param studentId 学生ID
     */
    void deleteRecordsByStudentId(Long studentId);

    /**
     * 删除获奖记录
     * @param id 获奖记录ID
     */
    void deleteRecord(Long id);

    /**
     * 根据获奖申请ID创建获奖记录
     * @param applicationId 获奖申请ID
     * @return 创建的获奖记录
     */
    StudentAwardRecord createRecordFromApplication(Long applicationId);
}
