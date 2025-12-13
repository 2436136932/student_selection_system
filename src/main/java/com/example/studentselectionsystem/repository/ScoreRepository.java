package com.example.studentselectionsystem.repository;

import com.example.studentselectionsystem.entity.Score;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * 成绩数据访问接口
 */
@Mapper
public interface ScoreRepository extends BaseMapper<Score> {

    /**
     * 根据学生ID查找成绩
     * @param studentId 学生ID
     * @return 成绩列表
     */
    @Select("SELECT * FROM scores WHERE student_id = #{studentId}")
    List<Score> selectByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据课程ID查找成绩
     * @param courseId 课程ID
     * @return 成绩列表
     */
    @Select("SELECT * FROM scores WHERE course_id = #{courseId}")
    List<Score> selectByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据学生ID和课程ID查找成绩
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 成绩信息
     */
    @Select("SELECT * FROM scores WHERE student_id = #{studentId} AND course_id = #{courseId}")
    Optional<Score> selectByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 根据学生ID和学期查找成绩
     * @param studentId 学生ID
     * @param semester 学期
     * @return 成绩列表
     */
    @Select("SELECT * FROM scores WHERE student_id = #{studentId} AND semester = #{semester}")
    List<Score> selectByStudentIdAndSemester(@Param("studentId") Long studentId, @Param("semester") String semester);

    /**
     * 根据课程ID和学期查找成绩
     * @param courseId 课程ID
     * @param semester 学期
     * @return 成绩列表
     */
    @Select("SELECT * FROM scores WHERE course_id = #{courseId} AND semester = #{semester}")
    List<Score> selectByCourseIdAndSemester(@Param("courseId") Long courseId, @Param("semester") String semester);

    /**
     * 根据学期查找成绩
     * @param semester 学期
     * @return 成绩列表
     */
    @Select("SELECT * FROM scores WHERE semester = #{semester}")
    List<Score> selectBySemester(@Param("semester") String semester);

    /**
     * 根据学生ID计算总分
     * @param studentId 学生ID
     * @return 总分
     */
    @Select("SELECT SUM(total_score) FROM scores WHERE student_id = #{studentId}")
    Double getTotalScoreByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据学生ID计算平均分
     * @param studentId 学生ID
     * @return 平均分
     */
    @Select("SELECT AVG(total_score) FROM scores WHERE student_id = #{studentId}")
    Double getAverageScoreByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据学生ID和学期计算总分
     * @param studentId 学生ID
     * @param semester 学期
     * @return 总分
     */
    @Select("SELECT SUM(total_score) FROM scores WHERE student_id = #{studentId} AND semester = #{semester}")
    Double getTotalScoreByStudentIdAndSemester(@Param("studentId") Long studentId, @Param("semester") String semester);

    /**
     * 根据学生ID和学期计算平均分
     * @param studentId 学生ID
     * @param semester 学期
     * @return 平均分
     */
    @Select("SELECT AVG(total_score) FROM scores WHERE student_id = #{studentId} AND semester = #{semester}")
    Double getAverageScoreByStudentIdAndSemester(@Param("studentId") Long studentId, @Param("semester") String semester);

    /**
     * 获取课程的平均成绩
     * @param courseId 课程ID
     * @return 平均成绩
     */
    @Select("SELECT AVG(total_score) FROM scores WHERE course_id = #{courseId}")
    Double getAverageScoreByCourseId(@Param("courseId") Long courseId);

    /**
     * 获取课程的平均成绩（按学期）
     * @param courseId 课程ID
     * @param semester 学期
     * @return 平均成绩
     */
    @Select("SELECT AVG(total_score) FROM scores WHERE course_id = #{courseId} AND semester = #{semester}")
    Double getAverageScoreByCourseIdAndSemester(@Param("courseId") Long courseId, @Param("semester") String semester);

}