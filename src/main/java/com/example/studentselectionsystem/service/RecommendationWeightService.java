package com.example.studentselectionsystem.service;

import com.example.studentselectionsystem.entity.RecommendationWeight;

public interface RecommendationWeightService {
    
    RecommendationWeight getWeights();
    
    RecommendationWeight saveWeights(RecommendationWeight weights, Long userId);
}
