package com.example.studentselectionsystem.controller;

import com.example.studentselectionsystem.entity.Student;
import com.example.studentselectionsystem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生筛选控制器，提供线性搜索筛选功能的API接口
 */
@RestController
@RequestMapping("/api/student-filter")
public class StudentFilterController {

    @Autowired
    private StudentService studentService;

    /**
     * 根据平均成绩筛选符合奖学金条件的学生
     * @param minAverageScore 最低平均成绩
     * @return 符合条件的学生列表
     */
    @GetMapping("/scholarship")
    public ResponseEntity<List<Student>> filterStudentsByScholarshipCriteria(
            @RequestParam(defaultValue = "80.0") double minAverageScore) {
        
        List<Student> eligibleStudents = studentService.filterStudentsByScholarshipCriteria(minAverageScore);
        return ResponseEntity.ok(eligibleStudents);
    }

    /**
     * 根据获奖情况筛选符合荣誉称号条件的学生
     * @param minAwardCount 最低获奖次数
     * @param awardLevel 奖项级别（可选）
     * @return 符合条件的学生列表
     */
    @GetMapping("/honor")
    public ResponseEntity<List<Student>> filterStudentsByHonorCriteria(
            @RequestParam(defaultValue = "1") int minAwardCount,
            @RequestParam(required = false) String awardLevel) {
        
        List<Student> eligibleStudents = studentService.filterStudentsByHonorCriteria(minAwardCount, awardLevel);
        return ResponseEntity.ok(eligibleStudents);
    }

    /**
     * 根据综合条件筛选学生
     * @param minAverageScore 最低平均成绩（可选）
     * @param minAwardCount 最低获奖次数（可选）
     * @param majorId 专业ID（可选）
     * @param year 年级（可选）
     * @param awardLevel 奖项级别（可选）
     * @return 符合条件的学生列表
     */
    @GetMapping("/comprehensive")
    public ResponseEntity<List<Student>> filterStudentsByComprehensiveCriteria(
            @RequestParam(required = false) Double minAverageScore,
            @RequestParam(required = false) Integer minAwardCount,
            @RequestParam(required = false) Integer majorId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String awardLevel) {
        
        List<Student> eligibleStudents = studentService.filterStudentsByComprehensiveCriteria(
                minAverageScore, minAwardCount, majorId, year, awardLevel);
        return ResponseEntity.ok(eligibleStudents);
    }
}
