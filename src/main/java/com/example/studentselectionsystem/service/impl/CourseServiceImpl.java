package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
            
            // 只更新那些不为null的字段
            if (course.getCourseCode() != null) {
                updatedCourse.setCourseCode(course.getCourseCode());
            }
            if (course.getCourseName() != null) {
                updatedCourse.setCourseName(course.getCourseName());
            }
            if (course.getCredits() != null) {
                updatedCourse.setCredits(course.getCredits());
            }
            if (course.getHours() != null) {
                updatedCourse.setHours(course.getHours());
            }
            if (course.getTeacherId() != null) {
                updatedCourse.setTeacherId(course.getTeacherId());
            }
            if (course.getSemester() != null) {
                updatedCourse.setSemester(course.getSemester());
            }
            if (course.getYear() != null) {
                updatedCourse.setYear(course.getYear());
            }
            if (course.getMaxStudents() != null) {
                updatedCourse.setMaxStudents(course.getMaxStudents());
            }
            if (course.getCurrentStudents() != null) {
                updatedCourse.setCurrentStudents(course.getCurrentStudents());
            }
            if (course.getStatus() != null) {
                updatedCourse.setStatus(course.getStatus());
            }
            if (course.getDescription() != null) {
                updatedCourse.setDescription(course.getDescription());
            }
            
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
        return Optional.ofNullable(courseRepository.selectByIdWithTeacher(id));
    }

    /**
     * 根据课程名称查找课程
     * @param courseName 课程名称
     * @return 课程信息
     */
    @Override
    public Optional<Course> findCourseByCourseName(String courseName) {
        return courseRepository.selectByCourseName(courseName);
    }

    /**
     * 根据课程代码查找课程
     * @param courseCode 课程代码
     * @return 课程信息
     */
    @Override
    public Optional<Course> findCourseByCourseCode(String courseCode) {
        return courseRepository.selectByCourseCode(courseCode);
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
     * 获取所有课程，包含教师信息
     * @return 课程列表
     */
    @Override
    public List<Course> findAllCoursesWithTeacher() {
        return courseRepository.selectAllWithTeacher();
    }

    /**
     * 分页获取课程
     * @param page 分页参数
     * @return 课程分页列表
     */
    @Override
    public IPage<Course> findCoursesByPage(IPage<Course> page) {
        return courseRepository.selectPageWithTeacher(page, null);
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
        // 执行分页查询，包含教师信息
        return courseRepository.selectPageWithTeacher(page, null);
    }
    
    /**
     * 分页获取课程，支持搜索条件
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @param courseCode 课程代码
     * @param courseName 课程名称
     * @param teacherName 教师名称
     * @return 课程分页列表
     */
    @Override
    public IPage<Course> findCoursesByPage(Integer current, Integer size, String courseCode, String courseName, String teacherName) {
        // 创建MyBatis Plus分页对象
        IPage<Course> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        
        // 构建查询条件
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        
        // 添加课程代码模糊查询
        if (courseCode != null && !courseCode.isEmpty()) {
            queryWrapper.like("course_code", courseCode);
        }
        
        // 添加课程名称模糊查询
        if (courseName != null && !courseName.isEmpty()) {
            queryWrapper.like("course_name", courseName);
        }
        
        // 添加教师名称模糊查询
        if (teacherName != null && !teacherName.isEmpty()) {
            queryWrapper.like("t.name", teacherName);
        }
        
        // 执行分页查询，包含教师信息
        return courseRepository.selectPageWithTeacher(page, queryWrapper);
    }

    /**
     * 判断课程名称是否存在
     * @param courseName 课程名称
     * @return 是否存在
     */
    @Override
    public boolean existsByCourseName(String courseName) {
        return courseRepository.existsByCourseName(courseName);
    }

    /**
     * 判断课程代码是否存在
     * @param courseCode 课程代码
     * @return 是否存在
     */
    @Override
    public boolean existsByCourseCode(String courseCode) {
        return courseRepository.existsByCourseCode(courseCode);
    }

}