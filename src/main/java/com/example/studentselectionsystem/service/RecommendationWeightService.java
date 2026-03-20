package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.studentselectionsystem.entity.RecommendationWeight;
import com.example.studentselectionsystem.mapper.RecommendationWeightMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RecommendationWeightService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationWeightService.class);

    @Autowired
    private RecommendationWeightMapper recommendationWeightMapper;

    public RecommendationWeight getWeights() {
        RecommendationWeight weights = recommendationWeightMapper.selectOne(
            new LambdaQueryWrapper<RecommendationWeight>().last("LIMIT 1")
        );
        
        if (weights == null) {
            weights = createDefaultWeights();
        }
        
        return weights;
    }

    public RecommendationWeight updateWeights(RecommendationWeight newWeights, Long userId) {
        RecommendationWeight existing = getWeights();
        
        if (existing.getId() == null) {
            newWeights.setUpdatedBy(userId);
            recommendationWeightMapper.insert(newWeights);
            return newWeights;
        }
        
        existing.setGradeWeight(newWeights.getGradeWeight());
        existing.setAwardWeight(newWeights.getAwardWeight());
        existing.setMajorWeight(newWeights.getMajorWeight());
        existing.setHistoryWeight(newWeights.getHistoryWeight());
        existing.setCompetitionWeight(newWeights.getCompetitionWeight());
        existing.setUpdatedBy(userId);
        
        recommendationWeightMapper.updateById(existing);
        return existing;
    }

    private RecommendationWeight createDefaultWeights() {
        RecommendationWeight weights = new RecommendationWeight();
        weights.setGradeWeight(new BigDecimal("40.00"));
        weights.setAwardWeight(new BigDecimal("30.00"));
        weights.setMajorWeight(new BigDecimal("15.00"));
        weights.setHistoryWeight(new BigDecimal("10.00"));
        weights.setCompetitionWeight(new BigDecimal("5.00"));
        
        recommendationWeightMapper.insert(weights);
        return weights;
    }

    public boolean validateWeights(RecommendationWeight weights) {
        BigDecimal total = weights.getGradeWeight()
            .add(weights.getAwardWeight())
            .add(weights.getMajorWeight())
            .add(weights.getHistoryWeight())
            .add(weights.getCompetitionWeight());
        
        return total.compareTo(new BigDecimal("100.00")) == 0;
    }
}
