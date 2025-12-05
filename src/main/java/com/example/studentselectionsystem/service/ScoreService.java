package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Score;

import java.util.List;
import java.util.Optional;

/**
 * 成绩服务接口
 */
public interface ScoreService {

    /**
     * 创建成绩记录
     * @param score 成绩信息
     * @return 创建的成绩
     */
    Score createScore(Score score);

    /**
     * 更新成绩信息
     * @param id 成绩ID
     * @param score 成绩信息
     * @return 更新后的成绩
     */
    Score updateScore(Long id, Score score);

    /**
     * 删除成绩
     * @param id 成绩ID
     */
    void deleteScore(Long id);

    /**
     * 根据ID查找成绩
     * @param id 成绩ID
     * @return 成绩信息
     */
    Optional<Score> findScoreById(Long id);

    /**
     * 根据学生ID查找成绩
     * @param studentId 学生ID
     * @return 成绩列表
     */
    List<Score> findScoresByStudentId(Long studentId);

    /**
     * 根据课程ID查找成绩
     * @param courseId 课程ID
     * @return 成绩列表
     */
    List<Score> findScoresByCourseId(Long courseId);

    /**
     * 根据学生ID和课程ID查找成绩
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 成绩信息
     */
    Optional<Score> findScoreByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * 根据学生ID和学期查找成绩
     * @param studentId 学生ID
     * @param semester 学期
     * @return 成绩列表
     */
    List<Score> findScoresByStudentIdAndSemester(Long studentId, String semester);

    /**
     * 根据课程ID和学期查找成绩
     * @param courseId 课程ID
     * @param semester 学期
     * @return 成绩列表
     */
    List<Score> findScoresByCourseIdAndSemester(Long courseId, String semester);

    /**
     * 根据学期查找成绩
     * @param semester 学期
     * @return 成绩列表
     */
    List<Score> findScoresBySemester(String semester);

    /**
     * 获取所有成绩
     * @return 成绩列表
     */
    List<Score> findAllScores();

    /**
     * 分页获取成绩
     * @param page 分页参数
     * @return 成绩分页列表
     */
    IPage<Score> findScoresByPage(IPage<Score> page);

    /**
     * 分页获取成绩
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @return 成绩分页列表
     */
    IPage<Score> findScoresByPage(Integer current, Integer size);

    /**
     * 分页获取成绩（带搜索条件）
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @param studentNumber 学生学号（可选）
     * @param courseCode 课程代码（可选）
     * @param semester 学期（可选）
     * @return 成绩分页列表
     */
    IPage<Score> findScoresByPage(Integer current, Integer size, String studentNumber, String courseCode, String semester);

    /**
     * 根据学生ID计算总分
     * @param studentId 学生ID
     * @return 总分
     */
    Double getTotalScoreByStudentId(Long studentId);

    /**
     * 根据学生ID计算平均分
     * @param studentId 学生ID
     * @return 平均分
     */
    Double getAverageScoreByStudentId(Long studentId);

    /**
     * 根据学生ID和学期计算总分
     * @param studentId 学生ID
     * @param semester 学期
     * @return 总分
     */
    Double getTotalScoreByStudentIdAndSemester(Long studentId, String semester);

    /**
     * 根据学生ID和学期计算平均分
     * @param studentId 学生ID
     * @param semester 学期
     * @return 平均分
     */
    Double getAverageScoreByStudentIdAndSemester(Long studentId, String semester);

    /**
     * 获取课程的平均成绩
     * @param courseId 课程ID
     * @return 平均成绩
     */
    Double getAverageScoreByCourseId(Long courseId);

    /**
     * 获取课程的平均成绩（按学期）
     * @param courseId 课程ID
     * @param semester 学期
     * @return 平均成绩
     */
    Double getAverageScoreByCourseIdAndSemester(Long courseId, String semester);

    /**
     * 使用冒泡排序算法按成绩降序排序
     * @param scores 成绩列表
     * @return 排序后的成绩列表
     */
    List<Score> bubbleSortByScoreDesc(List<Score> scores);

    /**
     * 使用冒泡排序算法按成绩升序排序
     * @param scores 成绩列表
     * @return 排序后的成绩列表
     */
    List<Score> bubbleSortByScoreAsc(List<Score> scores);
    
    /**
     * 获取成绩统计数据
     * @param studentNumber 学生学号（可选）
     * @param courseCode 课程代码（可选）
     * @param semester 学期（可选）
     * @return 统计数据
     */
    java.util.Map<String, Object> getScoreStatistics(String studentNumber, String courseCode, String semester);

}