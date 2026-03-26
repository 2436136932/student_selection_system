package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.studentselectionsystem.entity.RecommendationWeight;
import com.example.studentselectionsystem.mapper.RecommendationWeightMapper;
import com.example.studentselectionsystem.service.RecommendationWeightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class RecommendationWeightServiceImpl implements RecommendationWeightService {
    
    @Autowired
    private RecommendationWeightMapper recommendationWeightMapper;
    
    @Override
    public RecommendationWeight getWeights() {
        QueryWrapper<RecommendationWeight> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("updated_at");
        queryWrapper.last("LIMIT 1");
        RecommendationWeight weights = recommendationWeightMapper.selectOne(queryWrapper);
        
        if (weights == null) {
            weights = new RecommendationWeight();
            weights.setGradeWeight(new BigDecimal("40.00"));
            weights.setAwardWeight(new BigDecimal("30.00"));
            weights.setMajorWeight(new BigDecimal("15.00"));
            weights.setHistoryWeight(new BigDecimal("10.00"));
            weights.setCompetitionWeight(new BigDecimal("5.00"));
            weights.setUpdatedAt(LocalDateTime.now());
            recommendationWeightMapper.insert(weights);
        }
        
        return weights;
    }
    
    @Override
    public RecommendationWeight saveWeights(RecommendationWeight weights, Long userId) {
        weights.setUpdatedAt(LocalDateTime.now());
        weights.setUpdatedBy(userId);
        
        if (weights.getId() == null) {
            recommendationWeightMapper.insert(weights);
        } else {
            recommendationWeightMapper.updateById(weights);
        }
        
        return weights;
    }
}
