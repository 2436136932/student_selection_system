package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Teacher;

import java.util.List;
import java.util.Optional;

/**
 * 教师信息服务接口
 */
public interface TeacherService {

    /**
     * 创建教师
     * @param teacher 教师信息
     * @return 创建的教师
     */
    Teacher createTeacher(Teacher teacher);

    /**
     * 批量创建教师
     * @param teachers 教师列表
     * @return 创建的教师列表
     */
    List<Teacher> createTeachers(List<Teacher> teachers);

    /**
     * 更新教师信息
     * @param id 教师ID
     * @param teacher 教师信息
     * @return 更新后的教师
     */
    Teacher updateTeacher(Long id, Teacher teacher);

    /**
     * 删除教师
     * @param id 教师ID
     */
    void deleteTeacher(Long id);

    /**
     * 批量删除教师
     * @param ids 教师ID列表
     */
    void deleteTeachers(List<Long> ids);

    /**
     * 根据ID查找教师
     * @param id 教师ID
     * @return 教师信息
     */
    Optional<Teacher> findTeacherById(Long id);

    /**
     * 根据工号查找教师
     * @param teacherNumber 工号
     * @return 教师信息
     */
    Optional<Teacher> findTeacherByTeacherNumber(String teacherNumber);

    /**
     * 根据用户ID查找教师
     * @param userId 用户ID
     * @return 教师信息
     */
    Optional<Teacher> findTeacherByUserId(Long userId);

    /**
     * 根据姓名查找教师
     * @param name 姓名
     * @return 教师列表
     */
    List<Teacher> findTeachersByName(String name);

    /**
     * 根据姓名模糊查找教师
     * @param name 姓名
     * @return 教师列表
     */
    List<Teacher> findTeachersByNameContaining(String name);

    /**
     * 根据部门查找教师
     * @param department 部门
     * @return 教师列表
     */
    List<Teacher> findTeachersByDepartment(String department);

    /**
     * 获取所有教师
     * @return 教师列表
     */
    List<Teacher> findAllTeachers();

    /**
     * 分页获取教师
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @return 教师分页列表
     */
    IPage<Teacher> findTeachersByPage(Integer current, Integer size);

    /**
     * 分页和搜索获取教师
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @param teacherNumber 工号
     * @param name 姓名
     * @param department 部门
     * @return 教师分页列表
     */
    IPage<Teacher> findTeachersByPageWithSearch(Integer current, Integer size, String teacherNumber, String name, String department);

    /**
     * 检查工号是否已存在
     * @param teacherNumber 工号
     * @return 是否存在
     */
    boolean existsByTeacherNumber(String teacherNumber);

    /**
     * 获取教师总数
     * @return 教师总数
     */
    long countTeachers();
}
