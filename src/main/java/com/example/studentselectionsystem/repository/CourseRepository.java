package com.example.studentselectionsystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentselectionsystem.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * 课程数据访问接口
 */
@Mapper
public interface CourseRepository extends BaseMapper<Course> {

    /**
     * 根据课程名称查找课程
     * @param courseName 课程名称
     * @return 课程信息
     */
    @Select("SELECT c.*, t.name as teacher_name FROM courses c LEFT JOIN teachers t ON c.teacher_id = t.id WHERE c.course_name = #{courseName}")
    Optional<Course> selectByCourseName(@Param("courseName") String courseName);

    /**
     * 根据课程代码查找课程
     * @param courseCode 课程代码
     * @return 课程信息
     */
    @Select("SELECT c.*, t.name as teacher_name FROM courses c LEFT JOIN teachers t ON c.teacher_id = t.id WHERE c.course_code = #{courseCode}")
    Optional<Course> selectByCourseCode(@Param("courseCode") String courseCode);

    /**
     * 获取所有课程，包含教师信息
     * @return 课程列表
     */
    @Select("SELECT c.*, t.name as teacher_name FROM courses c LEFT JOIN teachers t ON c.teacher_id = t.id")
    List<Course> selectAllWithTeacher();

    /**
     * 判断课程名称是否存在
     * @param courseName 课程名称
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM courses WHERE course_name = #{courseName}")
    boolean existsByCourseName(@Param("courseName") String courseName);

    /**
     * 判断课程代码是否存在
     * @param courseCode 课程代码
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM courses WHERE course_code = #{courseCode}")
    boolean existsByCourseCode(@Param("courseCode") String courseCode);

}