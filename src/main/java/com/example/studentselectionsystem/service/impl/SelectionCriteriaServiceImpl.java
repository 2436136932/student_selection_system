package com.example.studentselectionsystem.service.impl;

import com.example.studentselectionsystem.entity.SelectionCriteria;
import com.example.studentselectionsystem.mapper.SelectionCriteriaMapper;
import com.example.studentselectionsystem.service.SelectionCriteriaService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 评选标准服务实现类
 */
@Service
@Transactional
public class SelectionCriteriaServiceImpl implements SelectionCriteriaService {

    @Autowired
    private SelectionCriteriaMapper selectionCriteriaMapper;

    @Override
    public SelectionCriteria createSelectionCriteria(SelectionCriteria selectionCriteria) {
        // 设置创建时间
        selectionCriteria.setCreateTime(new Date());
        selectionCriteriaMapper.insert(selectionCriteria);
        return selectionCriteria;
    }

    @Override
    public SelectionCriteria updateSelectionCriteria(Integer id, SelectionCriteria selectionCriteria) {
        SelectionCriteria existingCriteria = selectionCriteriaMapper.selectById(id);
        if (existingCriteria != null) {
            // 更新现有评选标准的属性
            existingCriteria.setCriterionName(selectionCriteria.getCriterionName());
            existingCriteria.setCriterionType(selectionCriteria.getCriterionType());
            existingCriteria.setWeight(selectionCriteria.getWeight());
            existingCriteria.setThreshold(selectionCriteria.getThreshold());
            selectionCriteriaMapper.updateById(existingCriteria);
            return existingCriteria;
        }
        return null;
    }

    @Override
    public void deleteSelectionCriteria(Integer id) {
        selectionCriteriaMapper.deleteById(id);
    }

    @Override
    public Optional<SelectionCriteria> findSelectionCriteriaById(Integer id) {
        SelectionCriteria criteria = selectionCriteriaMapper.selectById(id);
        return Optional.ofNullable(criteria);
    }

    @Override
    public List<SelectionCriteria> findSelectionCriteriaByAwardId(Integer awardId) {
        LambdaQueryWrapper<SelectionCriteria> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SelectionCriteria::getAwardId, awardId);
        return selectionCriteriaMapper.selectList(queryWrapper);
    }

    @Override
    public List<SelectionCriteria> findAllSelectionCriteria() {
        return selectionCriteriaMapper.selectList(null);
    }

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<SelectionCriteria> findSelectionCriteriaByPage(com.baomidou.mybatisplus.extension.plugins.pagination.Page<SelectionCriteria> page) {
        return selectionCriteriaMapper.selectPage(page, null);
    }

    @Override
    public Double calculateTotalWeightByAwardId(Integer awardId) {
        LambdaQueryWrapper<SelectionCriteria> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SelectionCriteria::getAwardId, awardId);
        List<SelectionCriteria> criteriaList = selectionCriteriaMapper.selectList(queryWrapper);
        return criteriaList.stream()
                .mapToDouble(criteria -> criteria.getWeight() != null ? criteria.getWeight().doubleValue() : 0.0)
                .sum();
    }
}
