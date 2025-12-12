package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.studentselectionsystem.entity.Award;
import com.example.studentselectionsystem.mapper.AwardMapper;
import com.example.studentselectionsystem.service.AwardTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 奖项类型服务实现类
 */
@Service
@Transactional
public class AwardTypeServiceImpl implements AwardTypeService {
    
    @Autowired
    private AwardMapper awardMapper;
    
    // 更新奖项的当前状态
    private void updateAwardCurrentStatus(Award award) {
        if (award == null) {
            return;
        }
        
        Date now = new Date();
        Date endTime = award.getEndTime();
        String status = award.getStatus();
        String currentStatus = award.getCurrentStatus();
        String newCurrentStatus = currentStatus;
        boolean statusChanged = false;
        
        // 检查结束时间是否已到
        boolean isEndTimeReached = endTime != null && now.after(endTime);
        
        // 根据奖项状态设置当前状态，同时考虑结束时间
        if ("未发布".equals(status)) {
            newCurrentStatus = "待开始";
        } else if ("已发布".equals(status)) {
            newCurrentStatus = isEndTimeReached ? "已关闭" : "进行中";
            // 如果已到结束时间，更新奖项状态为已结束
            if (isEndTimeReached && "已发布".equals(status)) {
                award.setStatus("已结束");
                statusChanged = true;
            }
        } else if ("已结束".equals(status)) {
            newCurrentStatus = isEndTimeReached ? "已关闭" : "已完成";
        }
        
        // 无论当前状态是否变化，都强制更新当前状态
        award.setCurrentStatus(newCurrentStatus);
        awardMapper.updateById(award);
    }
    
    // 更新奖项列表的当前状态
    private void updateAwardsCurrentStatus(List<Award> awards) {
        if (awards != null && !awards.isEmpty()) {
            awards.forEach(this::updateAwardCurrentStatus);
        }
    }

    @Override
    public long countAwardTypes() {
        // 使用MyBatis Plus的QueryWrapper统计不同奖项类型的数量
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Award> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.select("DISTINCT award_type");
        
        // 获取不同类型的列表并计算大小
        List<Award> distinctTypes = awardMapper.selectList(queryWrapper);
        return distinctTypes.size();
    }

    @Override
    public Optional<Award> getAwardTypeById(Integer id) {
        Award award = awardMapper.selectById(id);
        if (award != null) {
            updateAwardCurrentStatus(award);
        }
        return Optional.ofNullable(award);
    }

    @Override
    public List<Award> getAllAwardTypes() {
        // 获取所有奖项，不进行类型去重
        List<Award> awards = awardMapper.selectList(Wrappers.emptyWrapper());
        updateAwardsCurrentStatus(awards);
        return awards;
    }

    @Override
    public IPage<Award> getAwardTypesByPage(int page, int size, String awardName, String awardType) {
        // 创建MyBatis Plus的Page对象
        IPage<Award> pageResult = new Page<>(page, size);
        
        // 创建查询条件
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Award> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        
        // 添加奖项名称模糊查询条件
        if (awardName != null && !awardName.isEmpty()) {
            queryWrapper.like("award_name", awardName);
        }
        
        // 添加奖项类型精确查询条件
        if (awardType != null && !awardType.isEmpty()) {
            queryWrapper.eq("award_type", awardType);
        }
        
        // 使用MyBatis Plus的分页查询功能，直接查询符合条件的所有奖项，不进行类型去重
        pageResult = awardMapper.selectPage(pageResult, queryWrapper);
        
        // 更新奖项的当前状态
        updateAwardsCurrentStatus(pageResult.getRecords());
        
        return pageResult;
    }

    @Override
    public Award createAwardType(Award award) {
        awardMapper.insert(award);
        return award;
    }

    @Override
    public Award updateAwardType(Award award) {
        awardMapper.updateById(award);
        return award;
    }

    @Override
    public void deleteAwardType(Integer id) {
        awardMapper.deleteById(id);
    }
    
    @Override
    public Award publishAwardType(Integer id) {
        Award award = awardMapper.selectById(id);
        if (award != null) {
            award.setStatus("已发布"); // 设置奖项状态为已发布
            awardMapper.updateById(award);
            // 更新奖项的当前状态
            updateAwardCurrentStatus(award);
        }
        return award;
    }
}
