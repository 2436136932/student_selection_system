package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentselectionsystem.entity.Award;
import com.example.studentselectionsystem.mapper.AwardMapper;
import com.example.studentselectionsystem.service.AwardService;
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

    // 更新奖项的当前状态
    private void updateAwardCurrentStatus(com.example.studentselectionsystem.entity.Award entityAward) {
        if (entityAward == null) {
            return;
        }
        
        Date now = new Date();
        Date startTime = entityAward.getStartTime();
        Date endTime = entityAward.getEndTime();
        String status = entityAward.getStatus();
        String currentStatus = entityAward.getCurrentStatus();
        
        // 只有已发布的奖项才需要更新当前状态
        if ("已发布".equals(status)) {
            // 如果还没到开始时间
            if (startTime != null && now.before(startTime)) {
                if (!"未开始".equals(currentStatus)) {
                    entityAward.setCurrentStatus("未开始");
                    awardMapper.updateById(entityAward);
                }
            } 
            // 如果已经过了结束时间
            else if (endTime != null && now.after(endTime)) {
                if (!"已完成".equals(currentStatus)) {
                    entityAward.setCurrentStatus("已完成");
                    // 如果奖项状态还是已发布，更新为已结束
                    if ("已发布".equals(status)) {
                        entityAward.setStatus("已结束");
                    }
                    awardMapper.updateById(entityAward);
                }
            } 
            // 在开始时间和结束时间之间
            else {
                if (!"进行中".equals(currentStatus)) {
                    entityAward.setCurrentStatus("进行中");
                    awardMapper.updateById(entityAward);
                }
            }
        }
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
        // 设置当前状态和截止时间
        modelAward.setCurrentStatus(entityAward.getCurrentStatus());
        modelAward.setEndTime(entityAward.getEndTime());
        // 添加缺失的字段转换
        modelAward.setAwardType(entityAward.getAwardType());
        modelAward.setRequirement(entityAward.getRequirement());
        modelAward.setQuota(entityAward.getQuota());
        modelAward.setStartTime(entityAward.getStartTime());
        modelAward.setStatus(entityAward.getStatus());
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
        return awardMapper.insert(entityAward) > 0;
    }

    @Override
    public boolean updateAward(com.example.studentselectionsystem.model.Award award) {
        com.example.studentselectionsystem.entity.Award entityAward = new com.example.studentselectionsystem.entity.Award();
        entityAward.setId(Integer.parseInt(award.getAwardId()));
        entityAward.setAwardName(award.getAwardName());
        entityAward.setAwardLevel(award.getAwardLevel());
        entityAward.setDescription(award.getDescription());
        entityAward.setStatus(award.getStatus());
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
}