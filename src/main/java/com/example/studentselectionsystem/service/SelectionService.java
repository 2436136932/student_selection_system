package com.example.studentselectionsystem.service;

import com.example.studentselectionsystem.entity.Selection;

import java.util.List;

/**
 * 选课记录服务接口
 */
public interface SelectionService {

    /**
     * 创建选课记录
     * @param selection 选课记录信息
     * @return 创建的选课记录
     */
    Selection createSelection(Selection selection);

    /**
     * 删除选课记录
     * @param id 选课记录ID
     */
    void deleteSelection(Long id);

    /**
     * 根据课程ID删除所有选课记录
     * @param courseId 课程ID
     */
    void deleteSelectionsByCourseId(Long courseId);

    /**
     * 根据学生ID和课程ID删除选课记录
     * @param studentId 学生ID
     * @param courseId 课程ID
     */
    void deleteSelectionByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * 根据课程ID查找选课记录
     * @param courseId 课程ID
     * @return 选课记录列表
     */
    List<Selection> findSelectionsByCourseId(Long courseId);

    /**
     * 根据学生ID查找选课记录
     * @param studentId 学生ID
     * @return 选课记录列表
     */
    List<Selection> findSelectionsByStudentId(Long studentId);

    /**
     * 根据学生ID和课程ID查找选课记录
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 选课记录
     */
    Selection findSelectionByStudentIdAndCourseId(Long studentId, Long courseId);
}
