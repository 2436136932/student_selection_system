package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentselectionsystem.entity.StudentAwardApplication;

import java.util.List;
import java.util.Optional;

/**
 * 学生奖项申请服务接口
 */
public interface StudentAwardApplicationService {

    /**
     * 创建学生奖项申请
     * @param application 申请信息
     * @return 创建的申请
     */
    StudentAwardApplication createApplication(StudentAwardApplication application);

    /**
     * 更新申请状态
     * @param id 申请ID
     * @param status 新状态
     * @return 更新后的申请
     */
    StudentAwardApplication updateApplicationStatus(Integer id, Integer status);

    /**
     * 删除申请
     * @param id 申请ID
     */
    void deleteApplication(Integer id);

    /**
     * 根据ID查找申请
     * @param id 申请ID
     * @return 申请信息
     */
    Optional<StudentAwardApplication> findApplicationById(Integer id);

    /**
     * 根据学生ID查找申请
     * @param studentId 学生ID
     * @return 申请列表
     */
    List<StudentAwardApplication> findApplicationsByStudentId(Long studentId);

    /**
     * 根据奖项ID查找申请
     * @param awardId 奖项ID
     * @return 申请列表
     */
    List<StudentAwardApplication> findApplicationsByAwardId(Integer awardId);

    /**
     * 分页查找所有申请
     * @param page 分页参数
     * @return 申请分页结果
     */
    IPage<StudentAwardApplication> findAllApplications(Page<StudentAwardApplication> page);

    /**
     * 根据学生ID和奖项ID查找申请
     * @param studentId 学生ID
     * @param awardId 奖项ID
     * @return 申请信息
     */
    Optional<StudentAwardApplication> findApplicationByStudentAndAward(Long studentId, Integer awardId);

    /**
     * 检查学生是否已申请该奖项
     * @param studentId 学生ID
     * @param awardId 奖项ID
     * @return 是否已申请
     */
    boolean checkStudentApplicationExists(Long studentId, Integer awardId);

    /**
     * 获取所有申请总数
     * @return 申请总数
     */
    long countApplications();

    /**
     * 获取已批准的申请总数
     * @return 已批准申请总数
     */
    long countApprovedApplications();
}
