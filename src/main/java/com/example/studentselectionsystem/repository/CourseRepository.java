package com.example.studentselectionsystem.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    /**
     * 根据ID查询课程，包含教师信息
     * @param id 课程ID
     * @return 课程信息
     */
    @Select("SELECT c.*, t.name as teacher_name FROM courses c LEFT JOIN teachers t ON c.teacher_id = t.id WHERE c.id = #{id}")
    Course selectByIdWithTeacher(@Param("id") Long id);

    /**
     * 分页获取课程，包含教师信息
     * @param page 分页参数
     * @param queryWrapper 查询条件
     * @return 课程分页列表
     */
    @Select("SELECT c.*, t.name as teacher_name FROM courses c LEFT JOIN teachers t ON c.teacher_id = t.id ${ew.customSqlSegment}")
    IPage<Course> selectPageWithTeacher(IPage<Course> page, @Param("ew") QueryWrapper<Course> queryWrapper);

    /**
     * 根据教师ID分页查询课程，包含教师信息
     * @param page 分页参数
     * @param teacherId 教师ID
     * @return 课程分页列表
     */
    @Select("SELECT c.*, t.name as teacher_name FROM courses c LEFT JOIN teachers t ON c.teacher_id = t.id WHERE c.teacher_id = #{teacherId}")
    IPage<Course> selectByTeacherId(IPage<Course> page, @Param("teacherId") Long teacherId);

    /**
     * 根据学生ID分页查询课程，包含教师信息
     * @param page 分页参数
     * @param studentId 学生ID
     * @return 课程分页列表
     */
    @Select("SELECT c.*, t.name as teacher_name FROM courses c LEFT JOIN teachers t ON c.teacher_id = t.id INNER JOIN selections s ON c.id = s.course_id WHERE s.student_id = #{studentId} AND s.status = 1")
    IPage<Course> selectByStudentId(IPage<Course> page, @Param("studentId") Long studentId);

}