package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentselectionsystem.entity.Selection;
import com.example.studentselectionsystem.mapper.SelectionMapper;
import com.example.studentselectionsystem.service.SelectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 选课记录服务实现类
 */
@Service
@Transactional
public class SelectionServiceImpl extends ServiceImpl<SelectionMapper, Selection> implements SelectionService {

    /**
     * 创建选课记录
     * @param selection 选课记录信息
     * @return 创建的选课记录
     */
    @Override
    public Selection createSelection(Selection selection) {
        // 设置选课时间和状态
        selection.setSelectionTime(new Date());
        selection.setStatus(1); // 默认状态为正常
        baseMapper.insert(selection);
        return selection;
    }

    /**
     * 删除选课记录
     * @param id 选课记录ID
     */
    @Override
    public void deleteSelection(Long id) {
        baseMapper.deleteById(id);
    }

    /**
     * 根据课程ID删除所有选课记录
     * @param courseId 课程ID
     */
    @Override
    public void deleteSelectionsByCourseId(Long courseId) {
        baseMapper.deleteByCourseId(courseId);
    }

    /**
     * 根据学生ID和课程ID删除选课记录
     * @param studentId 学生ID
     * @param courseId 课程ID
     */
    @Override
    public void deleteSelectionByStudentIdAndCourseId(Long studentId, Long courseId) {
        Selection selection = baseMapper.findByStudentIdAndCourseId(studentId, courseId);
        if (selection != null) {
            baseMapper.deleteById(selection.getId());
        }
    }

    /**
     * 根据课程ID查找选课记录
     * @param courseId 课程ID
     * @return 选课记录列表
     */
    @Override
    public List<Selection> findSelectionsByCourseId(Long courseId) {
        return baseMapper.findByCourseId(courseId);
    }

    /**
     * 根据学生ID查找选课记录
     * @param studentId 学生ID
     * @return 选课记录列表
     */
    @Override
    public List<Selection> findSelectionsByStudentId(Long studentId) {
        return baseMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Selection>()
                .eq("student_id", studentId)
                .eq("status", 1));
    }

    /**
     * 根据学生ID和课程ID查找选课记录
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 选课记录
     */
    @Override
    public Selection findSelectionByStudentIdAndCourseId(Long studentId, Long courseId) {
        return baseMapper.findByStudentIdAndCourseId(studentId, courseId);
    }
}
