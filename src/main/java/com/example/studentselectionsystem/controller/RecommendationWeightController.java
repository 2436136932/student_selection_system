package com.example.studentselectionsystem.controller;

import com.example.studentselectionsystem.entity.RecommendationWeight;
import com.example.studentselectionsystem.service.RecommendationWeightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendation-weights")
public class RecommendationWeightController {
    
    @Autowired
    private RecommendationWeightService recommendationWeightService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getWeights() {
        RecommendationWeight weights = recommendationWeightService.getWeights();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", weights);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> saveWeights(@RequestBody RecommendationWeight weights) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Long userId = 1L;
        
        RecommendationWeight savedWeights = recommendationWeightService.saveWeights(weights, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", savedWeights);
        response.put("message", "权重配置保存成功");
        return ResponseEntity.ok(response);
    }
}
