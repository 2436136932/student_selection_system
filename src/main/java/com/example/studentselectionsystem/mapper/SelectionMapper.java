package com.example.studentselectionsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentselectionsystem.entity.Selection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

/**
 * 选课记录Mapper
 */
@Mapper
public interface SelectionMapper extends BaseMapper<Selection> {

    /**
     * 根据课程ID查找选课记录
     * @param courseId 课程ID
     * @return 选课记录列表
     */
    List<Selection> findByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据学生ID和课程ID查找选课记录
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 选课记录
     */
    Selection findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 根据课程ID删除所有选课记录
     * @param courseId 课程ID
     */
    @Delete("DELETE FROM selections WHERE course_id = #{courseId}")
    void deleteByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据学生ID删除所有选课记录
     * @param studentId 学生ID
     */
    @Delete("DELETE FROM selections WHERE student_id = #{studentId}")
    void deleteByStudentId(@Param("studentId") Long studentId);
}
