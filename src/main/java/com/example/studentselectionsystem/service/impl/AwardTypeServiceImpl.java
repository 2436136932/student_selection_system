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

    @Override
    public long countAwardTypes() {
        // 使用MyBatis Plus的QueryWrapper统计不同奖项类型的数量
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Award> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.select("DISTINCT type");
        
        // 获取不同类型的列表并计算大小
        List<Award> distinctTypes = awardMapper.selectList(queryWrapper);
        return distinctTypes.size();
    }

    @Override
    public Optional<Award> getAwardTypeById(Integer id) {
        return Optional.ofNullable(awardMapper.selectById(id));
    }

    @Override
    public List<Award> getAllAwardTypes() {
        return awardMapper.selectList(Wrappers.emptyWrapper());
    }

    @Override
    public IPage<Award> getAwardTypesByPage(int page, int size, String awardName, String awardType) {
        // 创建MyBatis Plus的Page对象
        IPage<Award> pageResult = new Page<>(page, size);
        
        // 先获取所有符合条件的奖项
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Award> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        
        // 添加奖项名称模糊查询条件
        if (awardName != null && !awardName.isEmpty()) {
            queryWrapper.like("award_name", awardName);
        }
        
        // 添加奖项类型精确查询条件
        if (awardType != null && !awardType.isEmpty()) {
            queryWrapper.eq("award_type", awardType);
        }
        
        // 获取所有符合条件的奖项
        List<Award> allAwards = awardMapper.selectList(queryWrapper);
        
        // 使用Set来存储不同的奖项类型
        Set<String> distinctTypes = new HashSet<>();
        List<Award> uniqueTypeAwards = new ArrayList<>();
        
        // 遍历所有奖项，只保留不同类型的奖项
        for (Award award : allAwards) {
            if (!distinctTypes.contains(award.getAwardType())) {
                distinctTypes.add(award.getAwardType());
                uniqueTypeAwards.add(award);
            }
        }
        
        // 计算总数
        pageResult.setTotal(uniqueTypeAwards.size());
        
        // 进行分页处理
        if (uniqueTypeAwards.isEmpty()) {
            pageResult.setRecords(new ArrayList<>());
        } else {
            int startIndex = (page - 1) * size;
            int endIndex = Math.min(startIndex + size, uniqueTypeAwards.size());
            List<Award> paginatedTypes = uniqueTypeAwards.subList(startIndex, endIndex);
            pageResult.setRecords(paginatedTypes);
        }
        
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
}
