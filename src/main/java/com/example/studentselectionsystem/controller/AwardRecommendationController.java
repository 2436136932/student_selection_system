package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.studentselectionsystem.entity.Student;
import com.example.studentselectionsystem.mapper.StudentMapper;
import com.example.studentselectionsystem.service.AwardRecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class AwardRecommendationController {

    private static final Logger logger = LoggerFactory.getLogger(AwardRecommendationController.class);

    @Autowired
    private AwardRecommendationService recommendationService;

    @Autowired
    private StudentMapper studentMapper;

    @GetMapping("/awards")
    public ResponseEntity<Map<String, Object>> getRecommendedAwards(@RequestParam(required = false) Long studentId, @RequestParam(required = false) Long userId) {
        try {
            logger.info("收到推荐请求 - studentId: {}, userId: {}", studentId, userId);
            
            // 如果提供了 userId，则根据 userId 查找学生 ID
            if (studentId == null && userId != null) {
                logger.info("根据 userId 查找学生信息: {}", userId);
                Student student = studentMapper.selectOne(
                    new LambdaQueryWrapper<Student>().eq(Student::getUserId, userId)
                );
                if (student == null) {
                    logger.warn("未找到 userId={} 对应的学生信息", userId);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "未找到对应的学生信息，请确认您是学生账号");
                    return ResponseEntity.ok(errorResponse);
                }
                studentId = student.getId();
                logger.info("找到学生 ID: {}", studentId);
            }
            
            if (studentId == null) {
                logger.warn("请求参数缺失 studentId 和 userId");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "请提供学生ID或用户ID");
                return ResponseEntity.ok(errorResponse);
            }
            
            logger.info("开始为学生 ID={} 生成推荐", studentId);
            List<Map<String, Object>> recommendations = recommendationService.recommendAwards(studentId);
            
            logger.info("生成了 {} 个推荐奖项", recommendations.size());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", recommendations);
            response.put("total", recommendations.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取推荐奖项失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取推荐奖项失败: " + e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/my-awards")
    public ResponseEntity<Map<String, Object>> getMyRecommendedAwards() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "请通过学生ID获取推荐");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取推荐奖项失败: " + e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }
}