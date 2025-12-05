package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Student;

import java.util.List;
import java.util.Optional;

/**
 * 学生信息服务接口
 */
public interface StudentService {

    /**
     * 创建学生
     * @param student 学生信息
     * @return 创建的学生
     */
    Student createStudent(Student student);

    /**
     * 批量创建学生
     * @param students 学生列表
     * @return 创建的学生列表
     */
    List<Student> createStudents(List<Student> students);

    /**
     * 更新学生信息
     * @param id 学生ID
     * @param student 学生信息
     * @return 更新后的学生
     */
    Student updateStudent(Long id, Student student);

    /**
     * 删除学生
     * @param id 学生ID
     */
    void deleteStudent(Long id);

    /**
     * 批量删除学生
     * @param ids 学生ID列表
     */
    void deleteStudents(List<Long> ids);

    /**
     * 根据ID查找学生
     * @param id 学生ID
     * @return 学生信息
     */
    Optional<Student> findStudentById(Long id);

    /**
     * 根据学号查找学生
     * @param studentId 学号
     * @return 学生信息
     */
    Optional<Student> findStudentByStudentId(String studentId);

    /**
     * 根据姓名查找学生
     * @param name 姓名
     * @return 学生列表
     */
    List<Student> findStudentsByName(String name);

    /**
     * 根据姓名模糊查找学生
     * @param name 姓名
     * @return 学生列表
     */
    List<Student> findStudentsByNameContaining(String name);

    /**
     * 根据专业名称查找学生
     * @param majorName 专业名称
     * @return 学生列表
     */
    List<Student> findStudentsByMajorName(String majorName);

    /**
     * 根据性别查找学生
     * @param gender 性别
     * @return 学生列表
     */
    List<Student> findStudentsByGender(String gender);

    /**
     * 获取所有学生
     * @return 学生列表
     */
    List<Student> findAllStudents();

    /**
     * 分页获取学生
     * @param page 分页参数
     * @return 学生分页列表
     */
    IPage<Student> findStudentsByPage(IPage<Student> page);

    /**
     * 根据ID获取学生及其专业信息
     * @param id 学生ID
     * @return 学生信息
     */
    Optional<Student> findStudentByIdWithMajor(Long id);

    /**
     * 获取所有学生及其专业信息
     * @return 学生列表
     */
    List<Student> findAllStudentsWithMajor();

    /**
     * 检查学号是否已存在
     * @param studentId 学号
     * @return 是否存在
     */
    boolean existsByStudentId(String studentId);

    /**
     * 分页获取学生
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @return 学生分页列表
     */
    com.baomidou.mybatisplus.core.metadata.IPage<Student> findStudentsByPage(Integer current, Integer size);

    /**
     * 分页和搜索获取学生
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @param studentNumber 学号
     * @param name 姓名
     * @param className 班级
     * @return 学生分页列表
     */
    com.baomidou.mybatisplus.core.metadata.IPage<Student> findStudentsByPageWithSearch(Integer page, Integer size, String studentNumber, String name, String className);

    /**
     * 线性搜索：根据平均成绩筛选符合奖学金条件的学生
     * @param minAverageScore 最低平均成绩
     * @return 符合条件的学生列表
     */
    List<Student> filterStudentsByScholarshipCriteria(double minAverageScore);

    /**
     * 线性搜索：根据获奖情况筛选符合荣誉称号条件的学生
     * @param minAwardCount 最低获奖次数
     * @param awardLevel 奖项级别（可选）
     * @return 符合条件的学生列表
     */
    List<Student> filterStudentsByHonorCriteria(int minAwardCount, String awardLevel);

    /**
     * 线性搜索：根据综合条件筛选学生
     * @param minAverageScore 最低平均成绩（可选）
     * @param minAwardCount 最低获奖次数（可选）
     * @param majorId 专业ID（可选）
     * @param year 年级（可选）
     * @param awardLevel 奖项级别（可选）
     * @return 符合条件的学生列表
     */
    List<Student> filterStudentsByComprehensiveCriteria(Double minAverageScore, Integer minAwardCount, Integer majorId, Integer year, String awardLevel);

    /**
     * 获取学生总数
     * @return 学生总数
     */
    long countStudents();

}