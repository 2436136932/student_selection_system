package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Course;
import com.example.studentselectionsystem.repository.CourseRepository;
import com.example.studentselectionsystem.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 课程服务实现类
 */
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    /**
     * 创建课程
     * @param course 课程信息
     * @return 创建的课程
     */
    @Override
    public Course createCourse(Course course) {
        courseRepository.insert(course);
        return course;
    }

    /**
     * 更新课程信息
     * @param id 课程ID
     * @param course 课程信息
     * @return 更新后的课程
     */
    @Override
    public Course updateCourse(Long id, Course course) {
        Optional<Course> existingCourse = Optional.ofNullable(courseRepository.selectById(id));
        if (existingCourse.isPresent()) {
            Course updatedCourse = existingCourse.get();
            updatedCourse.setName(course.getName());
            updatedCourse.setCredit(course.getCredit());
            updatedCourse.setSemester(course.getSemester());
            courseRepository.updateById(updatedCourse);
            return updatedCourse;
        }
        return null;
    }

    /**
     * 删除课程
     * @param id 课程ID
     */
    @Override
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    /**
     * 根据ID查找课程
     * @param id 课程ID
     * @return 课程信息
     */
    @Override
    public Optional<Course> findCourseById(Long id) {
        return Optional.ofNullable(courseRepository.selectById(id));
    }

    /**
     * 根据课程名称查找课程
     * @param name 课程名称
     * @return 课程信息
     */
    @Override
    public Optional<Course> findCourseByName(String name) {
        return courseRepository.selectByName(name);
    }

    /**
     * 根据课程代码查找课程
     * @param code 课程代码
     * @return 课程信息
     */
    @Override
    public Optional<Course> findCourseByCode(String code) {
        return courseRepository.selectByCode(code);
    }

    /**
     * 获取所有课程
     * @return 课程列表
     */
    @Override
    public List<Course> findAllCourses() {
        return courseRepository.selectList(null);
    }

    /**
     * 分页获取课程
     * @param page 分页参数
     * @return 课程分页列表
     */
    @Override
    public IPage<Course> findCoursesByPage(IPage<Course> page) {
        return courseRepository.selectPage(page, null);
    }

    /**
     * 分页获取课程
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @return 课程分页列表
     */
    @Override
    public IPage<Course> findCoursesByPage(Integer current, Integer size) {
        // 创建MyBatis Plus分页对象
        IPage<Course> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        // 执行分页查询
        return courseRepository.selectPage(page, null);
    }

    /**
     * 判断课程名称是否存在
     * @param name 课程名称
     * @return 是否存在
     */
    @Override
    public boolean existsByName(String name) {
        return courseRepository.existsByName(name);
    }

    /**
     * 判断课程代码是否存在
     * @param code 课程代码
     * @return 是否存在
     */
    @Override
    public boolean existsByCode(String code) {
        return courseRepository.existsByCode(code);
    }

}