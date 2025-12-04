package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentselectionsystem.entity.StudentAwardApplication;
import com.example.studentselectionsystem.mapper.StudentAwardApplicationMapper;
import com.example.studentselectionsystem.service.StudentAwardApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 学生奖项申请服务实现类
 */
@Service
@Transactional
public class StudentAwardApplicationServiceImpl implements StudentAwardApplicationService {

    @Autowired
    private StudentAwardApplicationMapper studentAwardApplicationMapper;

    @Override
    public StudentAwardApplication createApplication(StudentAwardApplication application) {
        // 设置申请时间和创建时间
        application.setApplicationTime(new Date());
        application.setCreateTime(new Date());
        application.setUpdateTime(new Date());
        application.setStatus(0); // 初始状态为待审核
        studentAwardApplicationMapper.insert(application);
        return application;
    }

    @Override
    public StudentAwardApplication updateApplicationStatus(Integer id, Integer status) {
        StudentAwardApplication application = studentAwardApplicationMapper.selectById(id);
        if (application != null) {
            application.setStatus(status);
            application.setUpdateTime(new Date());
            studentAwardApplicationMapper.updateById(application);
        }
        return application;
    }

    @Override
    public void deleteApplication(Integer id) {
        studentAwardApplicationMapper.deleteById(id);
    }

    @Override
    public Optional<StudentAwardApplication> findApplicationById(Integer id) {
        StudentAwardApplication application = studentAwardApplicationMapper.selectById(id);
        return Optional.ofNullable(application);
    }

    @Override
    public List<StudentAwardApplication> findApplicationsByStudentId(Long studentId) {
        // 使用MyBatis-Plus的Lambda条件构造器查询
        return studentAwardApplicationMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StudentAwardApplication>()
                        .eq(StudentAwardApplication::getStudentId, studentId)
        );
    }

    @Override
    public List<StudentAwardApplication> findApplicationsByAwardId(Integer awardId) {
        return studentAwardApplicationMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StudentAwardApplication>()
                        .eq(StudentAwardApplication::getAwardId, awardId)
        );
    }

    @Override
    public IPage<StudentAwardApplication> findAllApplications(Page<StudentAwardApplication> page) {
        return studentAwardApplicationMapper.selectPage(page, null);
    }

    @Override
    public Optional<StudentAwardApplication> findApplicationByStudentAndAward(Long studentId, Integer awardId) {
        StudentAwardApplication application = studentAwardApplicationMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StudentAwardApplication>()
                        .eq(StudentAwardApplication::getStudentId, studentId)
                        .eq(StudentAwardApplication::getAwardId, awardId)
        );
        return Optional.ofNullable(application);
    }

    @Override
    public boolean checkStudentApplicationExists(Long studentId, Integer awardId) {
        return studentAwardApplicationMapper.exists(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StudentAwardApplication>()
                        .eq(StudentAwardApplication::getStudentId, studentId)
                        .eq(StudentAwardApplication::getAwardId, awardId)
        );
    }

    @Override
    public long countApplications() {
        return studentAwardApplicationMapper.selectCount(null);
    }

    @Override
    public long countApprovedApplications() {
        // 假设状态1表示已批准
        return studentAwardApplicationMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StudentAwardApplication>()
                        .eq(StudentAwardApplication::getStatus, 1)
        );
    }
}
