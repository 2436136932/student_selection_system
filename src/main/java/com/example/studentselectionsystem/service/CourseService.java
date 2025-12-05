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
     * @param name 课程名称
     * @return 课程信息
     */
    Optional<Course> findCourseByName(String name);

    /**
     * 根据课程代码查找课程
     * @param code 课程代码
     * @return 课程信息
     */
    Optional<Course> findCourseByCode(String code);

    /**
     * 获取所有课程
     * @return 课程列表
     */
    List<Course> findAllCourses();

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
     * 判断课程名称是否存在
     * @param name 课程名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 判断课程代码是否存在
     * @param code 课程代码
     * @return 是否存在
     */
    boolean existsByCode(String code);

}