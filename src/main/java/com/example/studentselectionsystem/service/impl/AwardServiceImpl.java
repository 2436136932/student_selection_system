package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentselectionsystem.mapper.AwardMapper;
import com.example.studentselectionsystem.service.AwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    // 将实体类转换为模型类
     private com.example.studentselectionsystem.model.Award convertToModelAward(com.example.studentselectionsystem.entity.Award entityAward) {
         com.example.studentselectionsystem.model.Award modelAward = new com.example.studentselectionsystem.model.Award();
         modelAward.setAwardId(String.valueOf(entityAward.getId()));
         modelAward.setAwardName(entityAward.getAwardName());
         modelAward.setAwardLevel(entityAward.getAwardLevel());
         modelAward.setDescription(entityAward.getDescription());
         // 由于model.Award没有year字段，我们可以使用createTime的年份
         if (entityAward.getCreateTime() != null) {
             modelAward.setYear(String.valueOf(entityAward.getCreateTime().getYear() + 1900));
         }
         return modelAward;
     }

    @Override
    public boolean addAward(com.example.studentselectionsystem.model.Award award) {
        com.example.studentselectionsystem.entity.Award entityAward = new com.example.studentselectionsystem.entity.Award();
        entityAward.setAwardName(award.getAwardName());
        entityAward.setAwardLevel(award.getAwardLevel());
        entityAward.setDescription(award.getDescription());
        return awardMapper.insert(entityAward) > 0;
    }

    @Override
    public boolean updateAward(com.example.studentselectionsystem.model.Award award) {
        com.example.studentselectionsystem.entity.Award entityAward = new com.example.studentselectionsystem.entity.Award();
        entityAward.setId(Integer.parseInt(award.getAwardId()));
        entityAward.setAwardName(award.getAwardName());
        entityAward.setAwardLevel(award.getAwardLevel());
        entityAward.setDescription(award.getDescription());
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
        return entityAwards.stream().map(this::convertToModelAward).collect(Collectors.toList());
    }

    @Override
    public List<com.example.studentselectionsystem.model.Award> getAllAwards() {
        List<com.example.studentselectionsystem.entity.Award> entityAwards = awardMapper.selectList(null);
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
}