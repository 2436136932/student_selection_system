package com.example.studentselectionsystem.controller;

import com.example.studentselectionsystem.service.*;
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

    @Autowired
    private AwardService awardService;

    @Autowired
    private StudentAwardApplicationService studentAwardApplicationService;

    @Autowired
    private StudentAwardRecordService studentAwardRecordService;

    @Autowired
    private StudentService studentService;

    /**
     * 获取奖项级别分布统计
     * @return 奖项级别分布数据
     */
    @GetMapping("/award-level-distribution")
    public ResponseEntity<Map<String, Object>> getAwardLevelDistribution() {
        Map<String, Long> distribution = studentAwardApplicationService.getAwardLevelDistribution();
        return ResponseEntity.ok(Map.of("data", distribution));
    }

    /**
     * 获取奖项申请状态分布统计
     * @return 申请状态分布数据
     */
    @GetMapping("/application-status-distribution")
    public ResponseEntity<Map<String, Object>> getApplicationStatusDistribution() {
        Map<String, Long> distribution = studentAwardApplicationService.getApplicationStatusDistribution();
        return ResponseEntity.ok(Map.of("data", distribution));
    }

    /**
     * 获取各专业获奖情况统计
     * @return 各专业获奖情况数据
     */
    @GetMapping("/awards-by-major")
    public ResponseEntity<Map<String, Object>> getAwardsByMajor() {
        Map<String, Long> distribution = studentAwardRecordService.getAwardsByMajor();
        return ResponseEntity.ok(Map.of("data", distribution));
    }

    /**
     * 获取审批流程时间分析统计
     * @return 审批流程时间分析数据
     */
    @GetMapping("/approval-time-analysis")
    public ResponseEntity<Map<String, Object>> getApprovalTimeAnalysis() {
        Map<String, Long> timeAnalysis = studentAwardApplicationService.getApprovalTimeAnalysis();
        return ResponseEntity.ok(Map.of("data", timeAnalysis));
    }

    /**
     * 获取奖项申请趋势统计
     * @return 奖项申请趋势数据
     */
    @GetMapping("/application-trend")
    public ResponseEntity<Map<String, Object>> getApplicationTrend() {
        Map<String, Long> trend = studentAwardApplicationService.getApplicationTrend();
        return ResponseEntity.ok(Map.of("data", trend));
    }


}
