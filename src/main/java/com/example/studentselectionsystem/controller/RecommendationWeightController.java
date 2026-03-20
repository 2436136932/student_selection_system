package com.example.studentselectionsystem.controller;

import com.example.studentselectionsystem.entity.RecommendationWeight;
import com.example.studentselectionsystem.service.RecommendationWeightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendation-weights")
public class RecommendationWeightController {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationWeightController.class);

    @Autowired
    private RecommendationWeightService recommendationWeightService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getWeights() {
        try {
            RecommendationWeight weights = recommendationWeightService.getWeights();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", weights);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取推荐权重配置失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取推荐权重配置失败: " + e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateWeights(@RequestBody RecommendationWeight weights) {
        try {
            if (!recommendationWeightService.validateWeights(weights)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "权重总和必须为100%");
                return ResponseEntity.ok(errorResponse);
            }
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = null;
            if (authentication != null && authentication.getPrincipal() != null) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof org.springframework.security.core.userdetails.User) {
                    String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
                    userId = 1L;
                }
            }
            
            RecommendationWeight updated = recommendationWeightService.updateWeights(weights, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updated);
            response.put("message", "权重配置更新成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("更新推荐权重配置失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "更新推荐权重配置失败: " + e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }
}
