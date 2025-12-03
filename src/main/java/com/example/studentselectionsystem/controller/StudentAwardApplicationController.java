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
    @PreAuthorize("hasRole('student')")
    public ResponseEntity<StudentAwardApplication> createApplication(@RequestBody StudentAwardApplication application) {
        StudentAwardApplication createdApplication = studentAwardApplicationService.createApplication(application);
        return new ResponseEntity<>(createdApplication, HttpStatus.CREATED);
    }

    /**
     * 更新申请状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('admin') or hasRole('teacher')")
    public ResponseEntity<StudentAwardApplication> updateApplicationStatus(@PathVariable Integer id, @RequestParam Integer status) {
        StudentAwardApplication updatedApplication = studentAwardApplicationService.updateApplicationStatus(id, status);
        if (updatedApplication != null) {
            return new ResponseEntity<>(updatedApplication, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * 删除申请
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('student')")
    public ResponseEntity<Void> deleteApplication(@PathVariable Integer id) {
        studentAwardApplicationService.deleteApplication(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 根据ID查找申请
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('teacher') or hasRole('student')")
    public ResponseEntity<StudentAwardApplication> findApplicationById(@PathVariable Integer id) {
        Optional<StudentAwardApplication> optionalApplication = studentAwardApplicationService.findApplicationById(id);
        return optionalApplication.map(application -> new ResponseEntity<>(application, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 根据学生ID查找申请
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('admin') or hasRole('teacher') or hasRole('student')")
    public ResponseEntity<List<StudentAwardApplication>> findApplicationsByStudentId(@PathVariable Long studentId) {
        List<StudentAwardApplication> applications = studentAwardApplicationService.findApplicationsByStudentId(studentId);
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    /**
     * 根据奖项ID查找申请
     */
    @GetMapping("/award/{awardId}")
    @PreAuthorize("hasRole('admin') or hasRole('teacher')")
    public ResponseEntity<List<StudentAwardApplication>> findApplicationsByAwardId(@PathVariable Integer awardId) {
        List<StudentAwardApplication> applications = studentAwardApplicationService.findApplicationsByAwardId(awardId);
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    /**
     * 分页查找所有申请
     */
    @GetMapping("/page")
    @PreAuthorize("hasRole('admin') or hasRole('teacher')")
    public ResponseEntity<IPage<StudentAwardApplication>> findAllApplications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<StudentAwardApplication> pageParam = new Page<>(page, size);
        IPage<StudentAwardApplication> applicationsPage = studentAwardApplicationService.findAllApplications(pageParam);
        return new ResponseEntity<>(applicationsPage, HttpStatus.OK);
    }

    /**
     * 检查学生是否已申请该奖项
     */
    @GetMapping("/check-exists")
    @PreAuthorize("hasRole('student')")
    public ResponseEntity<Boolean> checkStudentApplicationExists(
            @RequestParam Long studentId,
            @RequestParam Integer awardId) {
        boolean exists = studentAwardApplicationService.checkStudentApplicationExists(studentId, awardId);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }
}
