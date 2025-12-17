package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentselectionsystem.entity.Award;
import com.example.studentselectionsystem.mapper.AwardMapper;
import com.example.studentselectionsystem.service.AwardService;
import com.example.studentselectionsystem.service.StudentAwardApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 评奖评优Service实现类
 */
@Service
public class AwardServiceImpl implements AwardService {
    
    @Autowired
    private AwardMapper awardMapper;

    @Autowired
    private StudentAwardApplicationService studentAwardApplicationService;

    // 更新奖项的当前状态
    private void updateAwardCurrentStatus(com.example.studentselectionsystem.entity.Award entityAward) {
        if (entityAward == null) {
            return;
        }
        
        Date now = new Date();
        Date endTime = entityAward.getEndTime();
        String status = entityAward.getStatus();
        String currentStatus = entityAward.getCurrentStatus();
        String newCurrentStatus = currentStatus;
        
        // 检查结束时间是否已到
        boolean isEndTimeReached = endTime != null && now.after(endTime);
        
        // 根据奖项状态设置当前状态，同时考虑结束时间
        if ("未发布".equals(status)) {
            newCurrentStatus = "待开始";
        } else if ("已发布".equals(status)) {
            newCurrentStatus = isEndTimeReached ? "已关闭" : "进行中";
            // 如果已到结束时间，更新奖项状态为已结束
            if (isEndTimeReached && "已发布".equals(status)) {
                entityAward.setStatus("已结束");
            }
        } else if ("已结束".equals(status)) {
            newCurrentStatus = isEndTimeReached ? "已关闭" : "已完成";
        }
        
        // 设置当前状态
        entityAward.setCurrentStatus(newCurrentStatus);
        
        // 设置当前阶段
        if ("待开始".equals(newCurrentStatus) || "已完成".equals(newCurrentStatus) || "已关闭".equals(newCurrentStatus)) {
            // 如果状态不是进行中，保持原始阶段或设置为默认值
            if (entityAward.getCurrentStage() == null || entityAward.getCurrentStage().isEmpty()) {
                entityAward.setCurrentStage("未开始");
            }
        } else {
            // 如果状态是进行中，根据业务逻辑设置阶段
            Long awardId = entityAward.getId();
            // 获取该奖项的所有申请
            List<com.example.studentselectionsystem.entity.StudentAwardApplication> applications = 
                studentAwardApplicationService.findApplicationsByAwardId(awardId);
            
            if (applications.isEmpty()) {
                // 没有学生申请，处于学生申请阶段
                entityAward.setCurrentStage("学生申请");
            } else {
                // 有学生申请
                boolean hasTeacherApproval = false;
                boolean hasPendingAdminApproval = false;
                boolean hasAllAdminApproved = true;
                
                for (com.example.studentselectionsystem.entity.StudentAwardApplication application : applications) {
                    // 检查是否有教师审批通过的申请
                    if (application.getTeacherApprovalStatus() != null && application.getTeacherApprovalStatus() == 1) {
                        hasTeacherApproval = true;
                    }
                    
                    // 检查管理员审批状态
                    if (application.getAdminApprovalStatus() == null || application.getAdminApprovalStatus() == 0) {
                        // 有管理员待审批的申请
                        hasPendingAdminApproval = true;
                        hasAllAdminApproved = false;
                    } else if (application.getAdminApprovalStatus() != 1) {
                        // 有管理员未通过的申请
                        hasAllAdminApproved = false;
                    }
                }
                
                if (hasTeacherApproval && hasPendingAdminApproval) {
                    // 有学生申请且有教师审批但有管理员待审批，处于管理员审批阶段
                    entityAward.setCurrentStage("管理员审批");
                } else if (hasAllAdminApproved) {
                    // 所有申请都已通过管理员审批，处于结果公示阶段
                    entityAward.setCurrentStage("结果公示");
                } else {
                    // 其他情况，处于教师审批阶段
                    entityAward.setCurrentStage("教师审批");
                }
            }
        }
        
        // 更新奖项信息
        awardMapper.updateById(entityAward);
    }

    // 将实体类转换为模型类
    private com.example.studentselectionsystem.model.Award convertToModelAward(com.example.studentselectionsystem.entity.Award entityAward) {
        // 更新奖项的当前状态
        updateAwardCurrentStatus(entityAward);
        
        com.example.studentselectionsystem.model.Award modelAward = new com.example.studentselectionsystem.model.Award();
        modelAward.setAwardId(String.valueOf(entityAward.getId()));
        modelAward.setAwardName(entityAward.getAwardName());
        modelAward.setAwardLevel(entityAward.getAwardLevel());
        modelAward.setDescription(entityAward.getDescription());
        // 由于model.Award没有year字段，我们可以使用createTime的年份
        if (entityAward.getCreateTime() != null) {
            modelAward.setYear(String.valueOf(entityAward.getCreateTime().getYear() + 1900));
        }
        // 设置当前状态、当前阶段和截止时间
        modelAward.setCurrentStatus(entityAward.getCurrentStatus());
        modelAward.setCurrentStage(entityAward.getCurrentStage());
        modelAward.setEndTime(entityAward.getEndTime());
        // 添加缺失的字段转换
        modelAward.setAwardType(entityAward.getAwardType());
        modelAward.setRequirement(entityAward.getRequirement());
        modelAward.setQuota(entityAward.getQuota());
        modelAward.setStartTime(entityAward.getStartTime());
        modelAward.setStatus(entityAward.getStatus());
        // 设置审批教师信息
        if (entityAward.getApprovingTeacherId() != null) {
            modelAward.setApprovingTeacherId(entityAward.getApprovingTeacherId());
        }
        if (entityAward.getApprovingTeacherName() != null) {
            modelAward.setApprovingTeacherName(entityAward.getApprovingTeacherName());
        }
        return modelAward;
    }

    @Override
    public boolean addAward(com.example.studentselectionsystem.model.Award award) {
        com.example.studentselectionsystem.entity.Award entityAward = new com.example.studentselectionsystem.entity.Award();
        entityAward.setAwardName(award.getAwardName());
        entityAward.setAwardLevel(award.getAwardLevel());
        entityAward.setAwardType(award.getAwardType());
        entityAward.setDescription(award.getDescription());
        entityAward.setRequirement(award.getRequirement());
        entityAward.setQuota(award.getQuota());
        entityAward.setStartTime(award.getStartTime());
        entityAward.setEndTime(award.getEndTime());
        entityAward.setStatus(award.getStatus());
        entityAward.setCreateTime(new Date());
        entityAward.setUpdateTime(new Date());
        // 设置审批教师信息
        if (award.getApprovingTeacherId() != null) {
            entityAward.setApprovingTeacherId(award.getApprovingTeacherId());
        }
        if (award.getApprovingTeacherName() != null) {
            entityAward.setApprovingTeacherName(award.getApprovingTeacherName());
        }
        return awardMapper.insert(entityAward) > 0;
    }

    @Override
    public boolean updateAward(com.example.studentselectionsystem.model.Award award) {
        com.example.studentselectionsystem.entity.Award entityAward = new com.example.studentselectionsystem.entity.Award();
        entityAward.setId(Long.parseLong(award.getAwardId()));
        entityAward.setAwardName(award.getAwardName());
        entityAward.setAwardLevel(award.getAwardLevel());
        entityAward.setDescription(award.getDescription());
        entityAward.setStatus(award.getStatus());
        entityAward.setCurrentStatus(award.getCurrentStatus());
        entityAward.setCurrentStage(award.getCurrentStage());
        entityAward.setQuota(award.getQuota());
        entityAward.setStartTime(award.getStartTime());
        entityAward.setEndTime(award.getEndTime());
        entityAward.setRequirement(award.getRequirement());
        entityAward.setAwardType(award.getAwardType());
        entityAward.setUpdateTime(new Date());
        // 设置审批教师信息
        if (award.getApprovingTeacherId() != null) {
            entityAward.setApprovingTeacherId(award.getApprovingTeacherId());
        } else {
            // 如果传入的审批教师ID为空，则清空该字段
            entityAward.setApprovingTeacherId(null);
        }
        if (award.getApprovingTeacherName() != null) {
            entityAward.setApprovingTeacherName(award.getApprovingTeacherName());
        } else {
            // 如果传入的审批教师姓名为空，则清空该字段
            entityAward.setApprovingTeacherName(null);
        }
        return awardMapper.updateById(entityAward) > 0;
    }

    @Override
    public boolean deleteAward(String awardId) {
        return awardMapper.deleteById(Integer.parseInt(awardId)) > 0;
    }

    @Override
    public Optional<com.example.studentselectionsystem.model.Award> getAwardById(String awardId) {
        com.example.studentselectionsystem.entity.Award entityAward = awardMapper.selectById(Integer.parseInt(awardId));
        if (entityAward != null) {
            // 更新奖项的当前状态
            updateAwardCurrentStatus(entityAward);
            return Optional.of(convertToModelAward(entityAward));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<com.example.studentselectionsystem.model.Award> getAwardsByStudentId(String studentId) {
        QueryWrapper<com.example.studentselectionsystem.entity.Award> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId);
        List<com.example.studentselectionsystem.entity.Award> entityAwards = awardMapper.selectList(wrapper);
        
        // 先更新所有奖项的当前状态，然后再转换为模型类
        entityAwards.forEach(this::updateAwardCurrentStatus);
        
        return entityAwards.stream().map(this::convertToModelAward).collect(Collectors.toList());
    }

    @Override
    public List<com.example.studentselectionsystem.model.Award> getAllAwards() {
        List<com.example.studentselectionsystem.entity.Award> entityAwards = awardMapper.selectList(null);
        
        // 先更新所有奖项的当前状态，然后再转换为模型类
        entityAwards.forEach(this::updateAwardCurrentStatus);
        
        return entityAwards.stream().map(this::convertToModelAward).collect(Collectors.toList());
    }

    @Override
    public IPage<com.example.studentselectionsystem.model.Award> getAwardsByPage(int page, int size, String studentId, String awardName, String year) {
        // 创建查询条件
        QueryWrapper<com.example.studentselectionsystem.entity.Award> wrapper = new QueryWrapper<>();
        
        if (studentId != null && !studentId.isEmpty()) {
            wrapper.eq("student_id", studentId);
        }
        
        if (awardName != null && !awardName.isEmpty()) {
            wrapper.like("award_name", awardName);
        }
        
        if (year != null && !year.isEmpty()) {
            wrapper.eq("year", year);
        }
        
        // 分页查询
        IPage<com.example.studentselectionsystem.entity.Award> entityPage = new Page<>(page, size);
        entityPage = awardMapper.selectPage(entityPage, wrapper);
        
        // 先更新所有奖项的当前状态，然后再转换为模型类
        entityPage.getRecords().forEach(this::updateAwardCurrentStatus);
        
        // 转换为模型类
        List<com.example.studentselectionsystem.model.Award> modelAwards = entityPage.getRecords().stream()
                .map(this::convertToModelAward)
                .collect(Collectors.toList());
        
        // 创建结果页
        IPage<com.example.studentselectionsystem.model.Award> resultPage = new Page<>(page, size);
        resultPage.setRecords(modelAwards);
        resultPage.setTotal(entityPage.getTotal());
        
        return resultPage;
    }

    @Override
    public int getAwardCountByStudentId(String studentId) {
        QueryWrapper<com.example.studentselectionsystem.entity.Award> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId);
        return awardMapper.selectCount(wrapper).intValue();
    }

    @Override
    public long countAwards() {
        try {
            // 使用MyBatis Plus的selectCount方法直接查询数据库中的奖项总数
            return awardMapper.selectCount(null);
        } catch (Exception e) {
            // 添加错误日志
            System.err.println("Error in countAwards: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<com.example.studentselectionsystem.model.Award> getRecentAwards() {
        // 创建查询条件，按创建时间倒序排列，获取前5条记录
        QueryWrapper<com.example.studentselectionsystem.entity.Award> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("created_at");
        wrapper.last("LIMIT 5");
        
        List<com.example.studentselectionsystem.entity.Award> entityAwards = awardMapper.selectList(wrapper);
        
        // 先更新所有奖项的当前状态，然后再转换为模型类
        entityAwards.forEach(this::updateAwardCurrentStatus);
        
        return entityAwards.stream().map(this::convertToModelAward).collect(Collectors.toList());
    }
    
    @Override
    public boolean publishAward(String awardId) {
        // 根据ID查询奖项
        com.example.studentselectionsystem.entity.Award entityAward = awardMapper.selectById(Integer.parseInt(awardId));
        if (entityAward == null) {
            return false;
        }
        
        // 更新奖项状态为已发布
        entityAward.setStatus("已发布");
        
        // 更新当前状态（会根据开始时间和结束时间自动计算）
        updateAwardCurrentStatus(entityAward);
        
        // 保存更新
        return awardMapper.updateById(entityAward) > 0;
    }
}