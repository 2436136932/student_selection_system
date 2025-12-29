package com.example.studentselectionsystem.controller;

import com.example.studentselectionsystem.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 统计数据控制器
 * 用于处理各类统计数据的API请求
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private AwardService awardService;

    @Autowired
    private StudentAwardApplicationService studentAwardApplicationService;

    @Autowired
    private StudentAwardRecordService studentAwardRecordService;

    @Autowired
    private StudentService studentService;

    /**
     * 获取系统概览统计数据
     * @return 系统概览数据
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverviewData() {
        try {
            // 获取学生总数
            long studentCount = studentService.countStudents();
            // 获取奖项总数
            long awardCount = awardService.countAwards();
            // 获取申请总数
            long applicationCount = studentAwardApplicationService.countApplications();
            // 获取已通过申请数
            long approvedCount = studentAwardApplicationService.countApprovedApplications();
            
            Map<String, Object> overviewData = new HashMap<>();
            overviewData.put("studentCount", studentCount);
            overviewData.put("awardCount", awardCount);
            overviewData.put("applicationCount", applicationCount);
            overviewData.put("approvedCount", approvedCount);
            
            return ResponseEntity.ok(Map.of("data", overviewData));
        } catch (Exception e) {
            logger.error("获取系统概览统计数据失败: {}", e.getMessage(), e);
            // 返回默认值
            Map<String, Object> defaultData = new HashMap<>();
            defaultData.put("studentCount", 0L);
            defaultData.put("awardCount", 0L);
            defaultData.put("applicationCount", 0L);
            defaultData.put("approvedCount", 0L);
            return ResponseEntity.ok(Map.of("data", defaultData));
        }
    }

    /**
     * 获取奖项级别分布统计
     * @return 奖项级别分布数据
     */
    @GetMapping("/award-level-distribution")
    public ResponseEntity<Map<String, Object>> getAwardLevelDistribution() {
        try {
            Map<String, Long> distribution = studentAwardApplicationService.getAwardLevelDistribution();
            return ResponseEntity.ok(Map.of("data", distribution));
        } catch (Exception e) {
            logger.error("获取奖项级别分布统计失败: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of("data", new HashMap<String, Long>()));
        }
    }

    /**
     * 获取奖项申请状态分布统计
     * @return 申请状态分布数据
     */
    @GetMapping("/application-status-distribution")
    public ResponseEntity<Map<String, Object>> getApplicationStatusDistribution() {
        try {
            Map<String, Long> distribution = studentAwardApplicationService.getApplicationStatusDistribution();
            return ResponseEntity.ok(Map.of("data", distribution));
        } catch (Exception e) {
            logger.error("获取奖项申请状态分布统计失败: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of("data", new HashMap<String, Long>()));
        }
    }

    /**
     * 获取各专业获奖情况统计
     * @return 各专业获奖情况数据
     */
    @GetMapping("/awards-by-major")
    public ResponseEntity<Map<String, Object>> getAwardsByMajor() {
        try {
            Map<String, Long> distribution = studentAwardRecordService.getAwardsByMajor();
            return ResponseEntity.ok(Map.of("data", distribution));
        } catch (Exception e) {
            logger.error("获取各专业获奖情况统计失败: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of("data", new HashMap<String, Long>()));
        }
    }

    /**
     * 获取审批流程时间分析统计
     * @return 审批流程时间分析数据
     */
    @GetMapping("/approval-time-analysis")
    public ResponseEntity<Map<String, Object>> getApprovalTimeAnalysis() {
        try {
            Map<String, Long> timeAnalysis = studentAwardApplicationService.getApprovalTimeAnalysis();
            return ResponseEntity.ok(Map.of("data", timeAnalysis));
        } catch (Exception e) {
            logger.error("获取审批流程时间分析统计失败: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of("data", new HashMap<String, Long>()));
        }
    }

    /**
     * 获取奖项申请趋势统计
     * @return 奖项申请趋势数据
     */
    @GetMapping("/application-trend")
    public ResponseEntity<Map<String, Object>> getApplicationTrend() {
        try {
            Map<String, Long> trend = studentAwardApplicationService.getApplicationTrend();
            return ResponseEntity.ok(Map.of("data", trend));
        } catch (Exception e) {
            logger.error("获取奖项申请趋势统计失败: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of("data", new HashMap<String, Long>()));
        }
    }


}
