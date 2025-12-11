package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentselectionsystem.entity.Student;
import com.example.studentselectionsystem.entity.StudentAwardApplication;
import com.example.studentselectionsystem.entity.Award;
import com.example.studentselectionsystem.mapper.StudentAwardApplicationMapper;
import com.example.studentselectionsystem.mapper.AwardMapper;
import com.example.studentselectionsystem.service.StudentAwardApplicationService;
import com.example.studentselectionsystem.service.StudentService;
import com.example.studentselectionsystem.service.AwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

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
    
    @Autowired
    private AwardService awardService;
    
    @Autowired
    private AwardMapper awardMapper;

    @Override
    public StudentAwardApplication createApplication(StudentAwardApplication application) {
        // 检查奖项是否存在且处于可申请状态
        Integer awardId = application.getAwardId();
        if (awardId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "奖项ID不能为空");
        }
        
        // 查询奖项信息
        Award award = awardMapper.selectById(awardId);
        if (award == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "奖项不存在");
        }
        
        // 检查奖项状态是否为已发布
        if (!"已发布".equals(award.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该奖项未发布，无法申请");
        }
        
        // 检查奖项当前状态是否为进行中
        if (!"进行中".equals(award.getCurrentStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该奖项当前不在申请时间范围内");
        }
        
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
            // 检查申请是否已通过教师审批，只有教师审批通过后才能进行管理员审批
            if (application.getStatus() != 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该申请尚未通过教师审批，无法进行管理员审批");
            }
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
        StudentAwardApplication application = studentAwardApplicationMapper.selectById(id);
        if (application != null) {
            // 只有未被教师审批的申请才能被删除
            if (application.getTeacherApprovalStatus() == null || application.getStatus() == 0) {
                studentAwardApplicationMapper.deleteById(id);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "申请已被审批，无法删除");
            }
        }
    }

    @Override
    public Optional<StudentAwardApplication> findApplicationById(Integer id) {
        StudentAwardApplication application = studentAwardApplicationMapper.selectById(id);
        return Optional.ofNullable(application);
    }

    @Override
    public List<StudentAwardApplication> findApplicationsByStudentId(Long studentId) {
        // 使用MyBatis-Plus的Lambda条件构造器查询
        List<StudentAwardApplication> applications = studentAwardApplicationMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StudentAwardApplication>()
                        .eq(StudentAwardApplication::getStudentId, studentId)
        );
        
        // 加载学生信息和奖项信息
        for (StudentAwardApplication application : applications) {
            // 加载学生信息
            Optional<Student> studentOptional = studentService.findStudentById(application.getStudentId());
            if (studentOptional.isPresent()) {
                application.setStudent(studentOptional.get());
            }
            
            // 加载奖项信息
            Award award = awardMapper.selectById(application.getAwardId());
            if (award != null) {
                application.setAward(award);
            }
        }
        
        return applications;
    }

    @Override
    public List<StudentAwardApplication> findApplicationsByStudentNumber(String studentNumber) {
        Optional<Student> studentOptional = studentService.findStudentByStudentNumber(studentNumber);
        if (studentOptional.isPresent()) {
            return findApplicationsByStudentId(studentOptional.get().getId());
        }
        return new ArrayList<>();
    }

    @Override
    public List<StudentAwardApplication> findApplicationsByAwardId(Integer awardId) {
        return studentAwardApplicationMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StudentAwardApplication>()
                        .eq(StudentAwardApplication::getAwardId, awardId)
        );
    }

    @Override
    public List<StudentAwardApplication> findAllApplications() {
        return studentAwardApplicationMapper.selectList(null);
    }
    
    @Override
    public IPage<StudentAwardApplication> findAllApplications(Page<StudentAwardApplication> page) {
        return studentAwardApplicationMapper.selectPage(page, null);
    }

    @Override
    public IPage<StudentAwardApplication> findApplicationsByCondition(Page<StudentAwardApplication> page, Integer awardId, String studentName, Integer status, String studentNumber, String awardName) {
        System.out.println("收到查询学生申请列表请求，参数：awardId=" + awardId + ", awardName=" + awardName + ", studentName=" + studentName + ", studentNumber=" + studentNumber + ", status=" + status + ", pageNum=" + page.getCurrent() + ", pageSize=" + page.getSize());
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

        // 如果提供了学号，先通过学号查找学生ID，然后添加到查询条件中
        if (studentNumber != null && !studentNumber.isEmpty()) {
            Optional<Student> studentOptional = studentService.findStudentByStudentNumber(studentNumber);
            if (studentOptional.isPresent()) {
                wrapper.eq(StudentAwardApplication::getStudentId, studentOptional.get().getId());
            } else {
                // 如果学号不存在，直接返回空结果
                return new Page<StudentAwardApplication>(page.getCurrent(), page.getSize()) {
                    @Override
                    public List<StudentAwardApplication> getRecords() {
                        return new ArrayList<>();
                    }
                    @Override
                    public long getTotal() {
                        return 0;
                    }
                    @Override
                    public long getPages() {
                        return 0;
                    }
                    @Override
                    public boolean hasPrevious() {
                        return false;
                    }
                    @Override
                    public boolean hasNext() {
                        return false;
                    }
                };
            }
        }

        // 如果提供了奖项名称，需要通过关联查询来实现
        // 这里我们先不添加到wrapper中，而是在查询结果中进行过滤
        // 因为StudentAwardApplication和Award是多对一关系，需要通过关联查询或事后过滤来实现

        // 按申请时间降序排序
        wrapper.orderByDesc(StudentAwardApplication::getApplicationTime);

        // 执行分页查询
        IPage<StudentAwardApplication> applicationPage = studentAwardApplicationMapper.selectPage(page, wrapper);

        // 获取所有申请
        List<StudentAwardApplication> applications = applicationPage.getRecords();
        List<StudentAwardApplication> filteredApplications = new ArrayList<>();

        // 遍历申请，查询学生和奖项信息，并进行过滤
        for (StudentAwardApplication application : applications) {
            // 查询学生信息
            Optional<Student> studentOptional = studentService.findStudentById(application.getStudentId());
            if (studentOptional.isPresent()) {
                application.setStudent(studentOptional.get());
            }
            
            // 查询奖项信息
            Award award = awardMapper.selectById(application.getAwardId());
            if (award != null) {
                // 设置为null以避免循环引用
                award.setStudentAwardApplications(null);
                award.setSelectionCriteria(null);
                award.setSelectionProcesses(null);
                award.setSelectionResults(null);
                
                application.setAward(award);
            }
            
            // 检查过滤条件
            boolean match = true;
            
            // 学生姓名过滤
            if (studentName != null && !studentName.isEmpty()) {
                if (!studentOptional.isPresent() || !studentOptional.get().getName().contains(studentName)) {
                    match = false;
                }
            }
            
            // 奖项名称过滤
            if (awardName != null && !awardName.isEmpty()) {
                if (award == null || !award.getAwardName().contains(awardName)) {
                    match = false;
                }
            }
            
            if (match) {
                filteredApplications.add(application);
            }
        }

        // 更新分页结果
        applicationPage.setRecords(filteredApplications);
        applicationPage.setTotal(filteredApplications.size());

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
    public Optional<StudentAwardApplication> findApplicationByStudentNumberAndAward(String studentNumber, Integer awardId) {
        Optional<Student> studentOptional = studentService.findStudentByStudentNumber(studentNumber);
        if (studentOptional.isPresent()) {
            return findApplicationByStudentAndAward(studentOptional.get().getId(), awardId);
        }
        return Optional.empty();
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
    public boolean checkStudentApplicationExistsByStudentNumber(String studentNumber, Integer awardId) {
        Optional<Student> studentOptional = studentService.findStudentByStudentNumber(studentNumber);
        if (studentOptional.isPresent()) {
            return checkStudentApplicationExists(studentOptional.get().getId(), awardId);
        }
        return false;
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
