package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentselectionsystem.entity.Student;
import com.example.studentselectionsystem.entity.StudentAwardApplication;
import com.example.studentselectionsystem.mapper.StudentAwardApplicationMapper;
import com.example.studentselectionsystem.service.StudentAwardApplicationService;
import com.example.studentselectionsystem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    
    @Autowired
    private StudentService studentService;

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
    public StudentAwardApplication teacherApproveApplication(Integer id, Integer status, String comments) {
        StudentAwardApplication application = studentAwardApplicationMapper.selectById(id);
        if (application != null) {
            application.setTeacherApprovalStatus(status);
            application.setTeacherApprovalTime(new Date());
            application.setTeacherApprovalComments(comments);
            application.setUpdateTime(new Date());
            
            // 根据教师审批结果设置不同的最终状态
            if (status == 2) {
                application.setStatus(2); // 教师审核拒绝
            } else if (status == 1) {
                application.setStatus(1); // 教师审核通过，待管理员审批
            }
            
            studentAwardApplicationMapper.updateById(application);
        }
        return application;
    }

    @Override
    public StudentAwardApplication adminApproveApplication(Integer id, Integer status, String comments) {
        StudentAwardApplication application = studentAwardApplicationMapper.selectById(id);
        if (application != null) {
            application.setAdminApprovalStatus(status);
            application.setAdminApprovalTime(new Date());
            application.setAdminApprovalComments(comments);
            
            // 根据管理员审批结果设置不同的最终状态
            if (status == 2) {
                application.setStatus(4); // 管理员审批拒绝
            } else if (status == 1) {
                application.setStatus(3); // 管理员审批通过
            }
            
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
    public IPage<StudentAwardApplication> findApplicationsByCondition(Page<StudentAwardApplication> page, Integer awardId, String studentName, Integer status) {
        // 使用MyBatis-Plus的Lambda条件构造器构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StudentAwardApplication> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();

        // 添加奖项ID条件
        if (awardId != null) {
            wrapper.eq(StudentAwardApplication::getAwardId, awardId);
        }

        // 添加审批状态条件
        if (status != null) {
            wrapper.eq(StudentAwardApplication::getStatus, status);
        }

        // 按申请时间降序排序
        wrapper.orderByDesc(StudentAwardApplication::getApplicationTime);

        // 执行分页查询
        IPage<StudentAwardApplication> applicationPage = studentAwardApplicationMapper.selectPage(page, wrapper);

        // 如果需要按学生姓名过滤，先获取所有申请，然后过滤出符合条件的申请
        if (studentName != null && !studentName.isEmpty()) {
            // 获取所有申请
            List<StudentAwardApplication> applications = applicationPage.getRecords();
            List<StudentAwardApplication> filteredApplications = new ArrayList<>();

            // 遍历申请，查询学生信息并过滤
            for (StudentAwardApplication application : applications) {
                // 查询学生信息
                Optional<Student> studentOptional = studentService.findStudentById(application.getStudentId());
                if (studentOptional.isPresent()) {
                    Student student = studentOptional.get();
                    // 将学生信息设置到申请对象中
                    application.setStudent(student);
                    // 检查学生姓名是否包含搜索关键词
                    if (student.getName().contains(studentName)) {
                        filteredApplications.add(application);
                    }
                }
            }

            // 更新分页结果
            applicationPage.setRecords(filteredApplications);
            applicationPage.setTotal(filteredApplications.size());
        } else {
            // 如果不需要按学生姓名过滤，仍然查询学生信息并设置到申请对象中
            List<StudentAwardApplication> applications = applicationPage.getRecords();
            for (StudentAwardApplication application : applications) {
                Optional<Student> studentOptional = studentService.findStudentById(application.getStudentId());
                if (studentOptional.isPresent()) {
                    application.setStudent(studentOptional.get());
                }
            }
        }

        return applicationPage;
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
        // 现在状态3表示已批准（管理员审批通过）
        return studentAwardApplicationMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StudentAwardApplication>()
                        .eq(StudentAwardApplication::getStatus, 3)
        );
    }
}
