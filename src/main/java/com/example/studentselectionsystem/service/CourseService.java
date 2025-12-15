package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Course;

import java.util.List;
import java.util.Optional;

/**
 * 课程服务接口
 */
public interface CourseService {

    /**
     * 创建课程
     * @param course 课程信息
     * @return 创建的课程
     */
    Course createCourse(Course course);

    /**
     * 更新课程信息
     * @param id 课程ID
     * @param course 课程信息
     * @return 更新后的课程
     */
    Course updateCourse(Long id, Course course);

    /**
     * 删除课程
     * @param id 课程ID
     */
    void deleteCourse(Long id);

    /**
     * 根据ID查找课程
     * @param id 课程ID
     * @return 课程信息
     */
    Optional<Course> findCourseById(Long id);

    /**
     * 根据课程名称查找课程
     * @param courseName 课程名称
     * @return 课程信息
     */
    Optional<Course> findCourseByCourseName(String courseName);

    /**
     * 根据课程代码查找课程
     * @param courseCode 课程代码
     * @return 课程信息
     */
    Optional<Course> findCourseByCourseCode(String courseCode);

    /**
     * 获取所有课程
     * @return 课程列表
     */
    List<Course> findAllCourses();

    /**
     * 获取所有课程，包含教师信息
     * @return 课程列表
     */
    List<Course> findAllCoursesWithTeacher();

    /**
     * 分页获取课程
     * @param page 分页参数
     * @return 课程分页列表
     */
    IPage<Course> findCoursesByPage(IPage<Course> page);

    /**
     * 分页获取课程
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @return 课程分页列表
     */
    IPage<Course> findCoursesByPage(Integer current, Integer size);
    
    /**
     * 分页获取课程，支持搜索条件
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @param courseCode 课程代码
     * @param courseName 课程名称
     * @param teacherName 教师名称
     * @return 课程分页列表
     */
    IPage<Course> findCoursesByPage(Integer current, Integer size, String courseCode, String courseName, String teacherName);

    /**
     * 判断课程名称是否存在
     * @param courseName 课程名称
     * @return 是否存在
     */
    boolean existsByCourseName(String courseName);

    /**
     * 判断课程代码是否存在
     * @param courseCode 课程代码
     * @return 是否存在
     */
    boolean existsByCourseCode(String courseCode);

    /**
     * 根据教师ID获取课程列表
     * @param teacherId 教师ID
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @return 课程分页列表
     */
    IPage<Course> findCoursesByTeacherId(Long teacherId, Integer current, Integer size);

    /**
     * 根据学生ID获取课程列表
     * @param studentId 学生ID
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @return 课程分页列表
     */
    IPage<Course> findCoursesByStudentId(Long studentId, Integer current, Integer size);

}