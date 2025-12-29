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
import com.example.studentselectionsystem.service.StudentAwardRecordService;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(StudentAwardApplicationServiceImpl.class);

    @Autowired
    private StudentAwardApplicationMapper studentAwardApplicationMapper;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private AwardService awardService;
    
    @Autowired
    private AwardMapper awardMapper;
    
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    
    @Autowired
    private StudentAwardRecordService studentAwardRecordService;

    @Override
    public StudentAwardApplication createApplication(StudentAwardApplication application) {
        // 检查奖项是否存在且处于可申请状态
        Long awardId = application.getAwardId();
        if (awardId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "奖项ID不能为空");
        }
        
        // 检查学生ID是否存在
        Long studentId = application.getStudentId();
        if (studentId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "学生ID不能为空");
        }
        
        // 检查该学生是否已经获得过该奖项（状态为已通过）
        StudentAwardApplication existingApplication = studentAwardApplicationMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StudentAwardApplication>()
                        .eq(StudentAwardApplication::getStudentId, studentId)
                        .eq(StudentAwardApplication::getAwardId, awardId)
                        .eq(StudentAwardApplication::getStatus, 3) // 3表示已通过管理员审批，即最终获奖
        );
        
        if (existingApplication != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "您已经申请通过该奖项，不可重复申请");
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
        
        // 打印接收到的原始申请对象，用于调试
        logger.info("Received application for creation: awardId='{}', studentId='{}', description='{}', hasMaterialPath='{}', materialName='{}', materialSize='{}', materialType='{}'",
                application.getAwardId(),
                application.getStudentId(),
                application.getDescription(),
                application.getMaterialPath() != null,
                application.getMaterialName(),
                application.getMaterialSize(),
                application.getMaterialType());
        
        // 确保所有材料字段都有值（如果为null，设置默认值）
        if (application.getMaterialPath() == null) {
            application.setMaterialPath("");
            logger.warn("MaterialPath was null, setting to empty string");
        }
        if (application.getMaterialName() == null) {
            application.setMaterialName("");
            logger.warn("MaterialName was null, setting to empty string");
        }
        if (application.getMaterialSize() == null) {
            application.setMaterialSize(0L);
            logger.warn("MaterialSize was null, setting to 0");
        }
        if (application.getMaterialType() == null) {
            application.setMaterialType("");
            logger.warn("MaterialType was null, setting to empty string");
        }
        if (application.getTeacherApprovalStatus() == null) {
            application.setTeacherApprovalStatus(0);
        }
        if (application.getAdminApprovalStatus() == null) {
            application.setAdminApprovalStatus(0);
        }
        
        // 设置申请时间和创建时间
        Date now = new Date();
        application.setApplicationTime(now);
        application.setCreateTime(now);
        application.setUpdateTime(now);
        application.setStatus(0); // 初始状态为待审核
        
        // 打印处理后的申请对象，确保所有字段都有值
        logger.info("Processed application ready for insertion - materialPath: '{}', materialName: '{}', materialSize: '{}', materialType: '{}'", 
                application.getMaterialPath(),
                application.getMaterialName(),
                application.getMaterialSize(),
                application.getMaterialType());
        
        // 打印完整的申请对象，用于调试
        logger.debug("Full processed application object: {}", application);
        
        // 直接使用MyBatis Plus的insert方法，确保所有字段都被正确设置
        int insertResult = studentAwardApplicationMapper.insert(application);
        
        if (insertResult > 0) {
            logger.info("Application created successfully with ID: {}, inserted {} rows", application.getId(), insertResult);
            
            // 重新查询数据库，获取最新数据
            StudentAwardApplication insertedApplication = studentAwardApplicationMapper.selectById(application.getId());
            
            if (insertedApplication != null) {
                logger.info("Retrieved inserted application with ID: {}", insertedApplication.getId());
                logger.info("Inserted application material info from DB: materialPath='{}', materialName='{}', materialSize='{}', materialType='{}'",
                        insertedApplication.getMaterialPath(),
                        insertedApplication.getMaterialName(),
                        insertedApplication.getMaterialSize(),
                        insertedApplication.getMaterialType());
                
                // 检查材料字段是否被正确保存
                if (insertedApplication.getMaterialPath() == null || insertedApplication.getMaterialPath().isEmpty()) {
                    logger.error("MaterialPath was NOT saved correctly, it's still null or empty");
                } else {
                    logger.info("MaterialPath was saved correctly: '{}'", insertedApplication.getMaterialPath());
                }
            } else {
                logger.error("Failed to retrieve inserted application");
            }
            
            return insertedApplication;
        } else {
            logger.error("Failed to insert application, no rows inserted");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "创建申请失败");
        }
    }

    @Override
    public StudentAwardApplication updateApplication(StudentAwardApplication application) {
        // 检查申请是否存在
        Long applicationId = application.getId();
        if (applicationId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "申请ID不能为空");
        }
        
        StudentAwardApplication existingApplication = studentAwardApplicationMapper.selectById(applicationId);
        if (existingApplication == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "申请不存在");
        }
        
        // 打印更新前后的材料信息对比
        logger.info("Updating application {} with material info - ", applicationId);
        logger.info("Old material info: materialPath='{}', materialName='{}', materialSize='{}', materialType='{}'",
                existingApplication.getMaterialPath() != null ? existingApplication.getMaterialPath() : "null",
                existingApplication.getMaterialName() != null ? existingApplication.getMaterialName() : "null",
                existingApplication.getMaterialSize() != null ? existingApplication.getMaterialSize() : "null",
                existingApplication.getMaterialType() != null ? existingApplication.getMaterialType() : "null");
        logger.info("New material info: materialPath='{}', materialName='{}', materialSize='{}', materialType='{}'",
                application.getMaterialPath() != null ? application.getMaterialPath() : "null",
                application.getMaterialName() != null ? application.getMaterialName() : "null",
                application.getMaterialSize() != null ? application.getMaterialSize() : "null",
                application.getMaterialType() != null ? application.getMaterialType() : "null");
        
        // 检查申请是否处于可编辑状态
        if (existingApplication.getStatus() != 0) { // 只有待审核状态可以编辑
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "申请已进入审批流程，无法编辑");
        }
        
        // 更新申请信息
        application.setUpdateTime(new Date());
        application.setCreateTime(existingApplication.getCreateTime());
        application.setApplicationTime(existingApplication.getApplicationTime());
        application.setStatus(existingApplication.getStatus());
        studentAwardApplicationMapper.updateById(application);
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
                // 管理员审批通过后，生成获奖记录
                studentAwardRecordService.createRecordFromApplication(application.getId());
            }
            
            application.setUpdateTime(new Date());
            studentAwardApplicationMapper.updateById(application);
        }
        return application;
    }

    @Override
    public void deleteApplication(Long id) {
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
    public Optional<StudentAwardApplication> findApplicationById(Long id) {
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
    public List<StudentAwardApplication> findApplicationsByAwardId(Long awardId) {
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
    public IPage<StudentAwardApplication> findApplicationsByCondition(Page<StudentAwardApplication> page, Long awardId, String studentName, Integer status, String studentNumber, String awardName, Long currentTeacherId) {
        System.out.println("收到查询学生申请列表请求，参数：awardId=" + awardId + ", awardName=" + awardName + ", studentName=" + studentName + ", studentNumber=" + studentNumber + ", status=" + status + ", currentTeacherId=" + currentTeacherId + ", pageNum=" + page.getCurrent() + ", pageSize=" + page.getSize());
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
                award.setSelectionProcesses(null);
                
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
            
            // 审批老师过滤：如果当前用户是教师，只显示该教师负责审批的奖项申请
            if (currentTeacherId != null && award != null) {
                if (award.getApprovingTeacherId() == null || !award.getApprovingTeacherId().equals(currentTeacherId)) {
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
    public Optional<StudentAwardApplication> findApplicationByStudentAndAward(Long studentId, Long awardId) {
        StudentAwardApplication application = studentAwardApplicationMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StudentAwardApplication>()
                        .eq(StudentAwardApplication::getStudentId, studentId)
                        .eq(StudentAwardApplication::getAwardId, awardId)
        );
        return Optional.ofNullable(application);
    }

    @Override
    public Optional<StudentAwardApplication> findApplicationByStudentNumberAndAward(String studentNumber, Long awardId) {
        Optional<Student> studentOptional = studentService.findStudentByStudentNumber(studentNumber);
        if (studentOptional.isPresent()) {
            return findApplicationByStudentAndAward(studentOptional.get().getId(), awardId);
        }
        return Optional.empty();
    }

    @Override
    public boolean checkStudentApplicationExists(Long studentId, Long awardId) {
        // 只检查学生是否已经有一个已经通过的申请（状态为3）
        return studentAwardApplicationMapper.exists(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StudentAwardApplication>()
                        .eq(StudentAwardApplication::getStudentId, studentId)
                        .eq(StudentAwardApplication::getAwardId, awardId)
                        .eq(StudentAwardApplication::getStatus, 3) // 3表示管理员审批通过，即最终获奖
        );
    }

    @Override
    public boolean checkStudentApplicationExistsByStudentNumber(String studentNumber, Long awardId) {
        Optional<Student> studentOptional = studentService.findStudentByStudentNumber(studentNumber);
        if (studentOptional.isPresent()) {
            return checkStudentApplicationExists(studentOptional.get().getId(), awardId);
        }
        return false;
    }

    @Override
    public long countApplicationsByAwardId(Long awardId) {
        return studentAwardApplicationMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StudentAwardApplication>()
                        .eq(StudentAwardApplication::getAwardId, awardId)
        );
    }

    @Override
    public long countApprovedApplicationsByAwardId(Long awardId) {
        return studentAwardApplicationMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StudentAwardApplication>()
                        .eq(StudentAwardApplication::getAwardId, awardId)
                        .eq(StudentAwardApplication::getStatus, 3) // 3表示管理员审批通过，即最终获奖
        );
    }

    @Override
    public long countTeacherApprovedApplicationsByAwardId(Long awardId) {
        // 返回实际的教师审核通过数量
        return studentAwardApplicationMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StudentAwardApplication>()
                        .eq(StudentAwardApplication::getAwardId, awardId)
                        .eq(StudentAwardApplication::getTeacherApprovalStatus, 1) // 1表示教师审核通过
        );
    }

    @Override
    public long countAdminApprovedApplicationsByAwardId(Long awardId) {
        return studentAwardApplicationMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StudentAwardApplication>()
                        .eq(StudentAwardApplication::getAwardId, awardId)
                        .eq(StudentAwardApplication::getAdminApprovalStatus, 1) // 1表示管理员审核通过
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

    @Override
    public java.util.Map<String, Long> getAwardLevelDistribution() {
        java.util.Map<String, Long> distribution = new java.util.HashMap<>();
        
        try {
            // 使用MyBatis Plus的SQL查询直接统计奖项级别分布
            // 1. 先查询所有奖项的级别和ID
            List<Award> awards = awardMapper.selectList(null);
            
            // 2. 构建奖项ID到级别的映射
            java.util.Map<Long, String> awardLevelMap = new java.util.HashMap<>();
            for (Award award : awards) {
                awardLevelMap.put(award.getId(), award.getAwardLevel());
            }
            
            // 3. 查询所有申请的奖项ID分布
            List<java.util.Map<String, Object>> awardIdCounts = studentAwardApplicationMapper.selectMaps(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<StudentAwardApplication>()
                            .select("award_id, count(*) as count")
                            .groupBy("award_id")
            );
            
            // 4. 统计不同级别奖项的数量
            for (java.util.Map<String, Object> awardIdCount : awardIdCounts) {
                Long awardId = Long.valueOf(awardIdCount.get("award_id").toString());
                Long count = Long.valueOf(awardIdCount.get("count").toString());
                String awardLevel = awardLevelMap.get(awardId);
                if (awardLevel != null) {
                    distribution.put(awardLevel, distribution.getOrDefault(awardLevel, 0L) + count);
                }
            }
        } catch (Exception e) {
            logger.error("获取奖项级别分布失败: {}", e.getMessage(), e);
        }
        
        return distribution;
    }

    @Override
    public java.util.Map<String, Long> getApplicationStatusDistribution() {
        java.util.Map<String, Long> distribution = new java.util.HashMap<>();
        
        // 状态映射
        java.util.Map<Integer, String> statusMap = new java.util.HashMap<>();
        statusMap.put(0, "待审核");
        statusMap.put(1, "教师审核通过，待管理员审批");
        statusMap.put(2, "教师审核拒绝");
        statusMap.put(3, "管理员审批通过");
        statusMap.put(4, "管理员审批拒绝");
        
        try {
            // 使用一个查询获取所有状态的数量
            List<java.util.Map<String, Object>> statusCounts = studentAwardApplicationMapper.selectMaps(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<StudentAwardApplication>()
                            .select("status, count(*) as count")
                            .groupBy("status")
            );
            
            // 构建状态计数映射
            java.util.Map<Integer, Long> statusCountMap = new java.util.HashMap<>();
            for (java.util.Map<String, Object> statusCount : statusCounts) {
                Integer status = Integer.valueOf(statusCount.get("status").toString());
                Long count = Long.valueOf(statusCount.get("count").toString());
                statusCountMap.put(status, count);
            }
            
            // 填充所有状态的计数，包括0计数的状态
            for (Integer status : statusMap.keySet()) {
                Long count = statusCountMap.getOrDefault(status, 0L);
                distribution.put(statusMap.get(status), count);
            }
        } catch (Exception e) {
            logger.error("获取申请状态分布失败: {}", e.getMessage(), e);
            // 发生错误时，使用默认值
            for (Integer status : statusMap.keySet()) {
                distribution.put(statusMap.get(status), 0L);
            }
        }
        
        return distribution;
    }

    @Override
    public java.util.Map<String, Long> getApprovalTimeAnalysis() {
        java.util.Map<String, Long> timeAnalysis = new java.util.HashMap<>();
        
        // 查询所有申请，放宽条件，不限制状态
        List<StudentAwardApplication> applications = studentAwardApplicationMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StudentAwardApplication>()
        );
        
        logger.info("查询到的申请总数: {}", applications.size());
        
        // 提取所有申请的奖项ID，去重后查询所有奖项
        java.util.Set<Long> awardIds = new java.util.HashSet<>();
        for (StudentAwardApplication application : applications) {
            awardIds.add(application.getAwardId());
        }
        
        logger.info("涉及的奖项ID: {}", awardIds);
        
        // 构建奖项ID到奖项对象的映射
        java.util.Map<Long, Award> awardMap = new java.util.HashMap<>();
        if (!awardIds.isEmpty()) {
            List<Award> awards = awardMapper.selectBatchIds(awardIds);
            for (Award award : awards) {
                awardMap.put(award.getId(), award);
                logger.info("奖项ID: {}, endTime: {}", award.getId(), award.getEndTime());
            }
        }
        
        long totalApplicationToTeacher = 0;
        long totalTeacherToAdmin = 0;
        long totalAdminToFinal = 0;
        int appToTeacherCount = 0;
        int teacherToAdminCount = 0;
        int adminToFinalCount = 0;
        
        for (StudentAwardApplication application : applications) {
            logger.info("处理申请ID: {}, awardId: {}, applicationTime: {}, teacherApprovalTime: {}, adminApprovalTime: {}", 
                application.getId(), application.getAwardId(), application.getApplicationTime(), 
                application.getTeacherApprovalTime(), application.getAdminApprovalTime());
            
            // 计算申请到教师审批的时间差
            if (application.getApplicationTime() != null && application.getTeacherApprovalTime() != null) {
                long timeDiff = application.getTeacherApprovalTime().getTime() - application.getApplicationTime().getTime();
                totalApplicationToTeacher += timeDiff;
                appToTeacherCount++;
                logger.info("申请到教师审批时间差: {}毫秒, 当前计数: {}", timeDiff, appToTeacherCount);
            }
            
            // 计算教师审批到管理员审批的时间差
            if (application.getTeacherApprovalTime() != null && application.getAdminApprovalTime() != null) {
                long timeDiff = application.getAdminApprovalTime().getTime() - application.getTeacherApprovalTime().getTime();
                totalTeacherToAdmin += timeDiff;
                teacherToAdminCount++;
                logger.info("教师审批到管理员审批时间差: {}毫秒, 当前计数: {}", timeDiff, teacherToAdminCount);
            }
            
            // 计算管理员审批到最终结果的时间差：使用奖项的endTime减去管理员审批时间
            if (application.getAdminApprovalTime() != null) {
                Award award = awardMap.get(application.getAwardId());
                if (award != null && award.getEndTime() != null) {
                    long timeDiff = award.getEndTime().getTime() - application.getAdminApprovalTime().getTime();
                    totalAdminToFinal += timeDiff;
                    adminToFinalCount++;
                    logger.info("管理员审批到最终结果时间差: {}毫秒, 当前计数: {}", timeDiff, adminToFinalCount);
                } else {
                    logger.info("管理员审批到最终结果时间差计算失败: award={}, award.endTime={}", award, award != null ? award.getEndTime() : null);
                }
            }
        }
        
        // 分别计算各阶段的平均时间（转换为分钟，四舍五入）
        double avgAppToTeacher = appToTeacherCount > 0 ? (double) totalApplicationToTeacher / appToTeacherCount / (1000 * 60) : 0;
        double avgTeacherToAdmin = teacherToAdminCount > 0 ? (double) totalTeacherToAdmin / teacherToAdminCount / (1000 * 60) : 0;
        double avgAdminToFinal = adminToFinalCount > 0 ? (double) totalAdminToFinal / adminToFinalCount / (1000 * 60) : 0;
        
        logger.info("平均时间计算结果: 申请到教师审批={}分钟, 教师审批到管理员审批={}分钟, 管理员审批到最终结果={}分钟", 
            avgAppToTeacher, avgTeacherToAdmin, avgAdminToFinal);
        
        // 向上取整到整数分钟，确保不足1分钟的也显示为1分钟
        timeAnalysis.put("申请到教师审批", Math.max(1, Math.round(avgAppToTeacher)));
        timeAnalysis.put("教师审批到管理员审批", Math.max(1, Math.round(avgTeacherToAdmin)));
        timeAnalysis.put("管理员审批到最终结果", Math.round(avgAdminToFinal));
        
        logger.info("最终返回的审批时间分析数据: {}", timeAnalysis);
        
        return timeAnalysis;
    }

    @Override
    public java.util.Map<String, Long> getApplicationTrend() {
        java.util.Map<String, Long> trend = new java.util.LinkedHashMap<>();
        
        try {
            // 使用MyBatis Plus的SQL查询直接按月份统计申请数量
            List<java.util.Map<String, Object>> monthCounts = studentAwardApplicationMapper.selectMaps(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<StudentAwardApplication>()
                            .select("DATE_FORMAT(COALESCE(application_time, created_at), '%Y-%m') as month, count(*) as count")
                            .groupBy("DATE_FORMAT(COALESCE(application_time, created_at), '%Y-%m')")
                            .orderByAsc("DATE_FORMAT(COALESCE(application_time, created_at), '%Y-%m')")
            );
            
            // 构建月份计数映射
            for (java.util.Map<String, Object> monthCount : monthCounts) {
                String month = monthCount.get("month").toString();
                Long count = Long.valueOf(monthCount.get("count").toString());
                trend.put(month, count);
            }
        } catch (Exception e) {
            logger.error("获取奖项申请趋势失败: {}", e.getMessage(), e);
        }
        
        // 如果没有数据，添加最近6个月的默认数据，值为0
        if (trend.isEmpty()) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            for (int i = 5; i >= 0; i--) {
                java.util.Calendar tempCal = java.util.Calendar.getInstance();
                tempCal.add(java.util.Calendar.MONTH, -i);
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM");
                String month = sdf.format(tempCal.getTime());
                trend.put(month, 0L);
            }
        }
        
        return trend;
    }

    @Override
    public long getTotalApplicationCount() {
        return countApplications();
    }

    @Override
    public long getApprovedApplicationCount() {
        return countApprovedApplications();
    }
}
