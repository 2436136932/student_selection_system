package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentselectionsystem.entity.StudentAwardApplication;
import com.example.studentselectionsystem.service.StudentAwardApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 学生奖项申请控制器
 */
@RestController
@RequestMapping("/api/student-award-applications")
public class StudentAwardApplicationController {

    @Autowired
    private StudentAwardApplicationService studentAwardApplicationService;

    /**
     * 创建学生奖项申请
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentAwardApplication> createApplication(@RequestBody StudentAwardApplication application) {
        StudentAwardApplication createdApplication = studentAwardApplicationService.createApplication(application);
        return new ResponseEntity<>(createdApplication, HttpStatus.CREATED);
    }

    /**
     * 更新申请状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<StudentAwardApplication> updateApplicationStatus(@PathVariable Integer id, @RequestParam Integer status) {
        StudentAwardApplication updatedApplication = studentAwardApplicationService.updateApplicationStatus(id, status);
        if (updatedApplication != null) {
            return new ResponseEntity<>(updatedApplication, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * 教师审批申请
     */
    @PutMapping("/{id}/teacher-approve")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<StudentAwardApplication> teacherApproveApplication(
            @PathVariable Integer id,
            @RequestParam Integer status,
            @RequestParam(required = false) String comments) {
        StudentAwardApplication updatedApplication = studentAwardApplicationService.teacherApproveApplication(id, status, comments);
        if (updatedApplication != null) {
            return new ResponseEntity<>(updatedApplication, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * 管理员审批申请
     */
    @PutMapping("/{id}/admin-approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentAwardApplication> adminApproveApplication(
            @PathVariable Integer id,
            @RequestParam Integer status,
            @RequestParam(required = false) String comments) {
        StudentAwardApplication updatedApplication = studentAwardApplicationService.adminApproveApplication(id, status, comments);
        if (updatedApplication != null) {
            return new ResponseEntity<>(updatedApplication, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * 删除申请
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<Void> deleteApplication(@PathVariable Integer id) {
        studentAwardApplicationService.deleteApplication(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 根据ID查找申请
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<StudentAwardApplication> findApplicationById(@PathVariable Integer id) {
        Optional<StudentAwardApplication> optionalApplication = studentAwardApplicationService.findApplicationById(id);
        return optionalApplication.map(application -> new ResponseEntity<>(application, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 根据学生ID查找申请
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<List<StudentAwardApplication>> findApplicationsByStudentId(@PathVariable Long studentId) {
        List<StudentAwardApplication> applications = studentAwardApplicationService.findApplicationsByStudentId(studentId);
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    /**
     * 根据奖项ID查找申请
     */
    @GetMapping("/award/{awardId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<StudentAwardApplication>> findApplicationsByAwardId(@PathVariable Integer awardId) {
        List<StudentAwardApplication> applications = studentAwardApplicationService.findApplicationsByAwardId(awardId);
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    /**
     * 分页查找所有申请
     */
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<IPage<StudentAwardApplication>> findAllApplications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<StudentAwardApplication> pageParam = new Page<>(page, size);
        IPage<StudentAwardApplication> applicationsPage = studentAwardApplicationService.findAllApplications(pageParam);
        return new ResponseEntity<>(applicationsPage, HttpStatus.OK);
    }

    @GetMapping("/page/search")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN', 'TEACHER', 'ADMIN')")
    public ResponseEntity<IPage<StudentAwardApplication>> searchApplications(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer awardId,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) Integer status) {
        Page<StudentAwardApplication> page = new Page<>(pageNum, pageSize);
        IPage<StudentAwardApplication> applicationPage = 
            studentAwardApplicationService.findApplicationsByCondition(page, awardId, studentName, status);
        return ResponseEntity.ok(applicationPage);
    }

    /**
     * 检查学生是否已申请该奖项
     */
    @GetMapping("/check-exists")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Boolean> checkStudentApplicationExists(
            @RequestParam Long studentId,
            @RequestParam Integer awardId) {
        boolean exists = studentAwardApplicationService.checkStudentApplicationExists(studentId, awardId);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    /**
     * 获取申请总数
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getApplicationCount(
            @RequestParam(required = false) String status) {
        try {
            long count;
            if ("approved".equals(status)) {
                count = studentAwardApplicationService.countApprovedApplications();
            } else {
                count = studentAwardApplicationService.countApplications();
            }
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // 输出详细错误信息到控制台
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
