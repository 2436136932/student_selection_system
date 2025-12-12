package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentselectionsystem.entity.StudentAwardApplication;
import com.example.studentselectionsystem.service.StudentAwardApplicationService;
import com.example.studentselectionsystem.service.UserService;
import com.example.studentselectionsystem.entity.User;
import com.example.studentselectionsystem.entity.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * 学生奖项申请控制器
 */
@RestController
@RequestMapping("/api/student-award-applications")
public class StudentAwardApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(StudentAwardApplicationController.class);

    @Autowired
    private StudentAwardApplicationService studentAwardApplicationService;
    
    @Autowired
    private com.example.studentselectionsystem.service.StudentService studentService;
    
    @Autowired
    private UserService userService;

    /**
     * 创建学生奖项申请
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentAwardApplication> createApplication(@RequestBody StudentAwardApplication application, Principal principal) {
        try {
            logger.info("Creating award application: {}", application);
            
            // 从当前登录用户获取学生信息
            if (principal != null) {
                Optional<User> optionalUser = userService.findUserByUsername(principal.getName());
                if (optionalUser.isPresent()) {
                    Optional<Student> optionalStudent = studentService.findStudentByUserId(optionalUser.get().getId());
                    if (optionalStudent.isPresent()) {
                        Long studentId = optionalStudent.get().getId();
                        logger.info("Using studentId: {}", studentId);
                        application.setStudentId(studentId);
                    } else {
                        logger.error("Student not found for user: {}", principal.getName());
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                } else {
                    logger.error("User not found: {}", principal.getName());
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }
            
            StudentAwardApplication createdApplication = studentAwardApplicationService.createApplication(application);
            logger.info("Created award application: {}", createdApplication);
            return new ResponseEntity<>(createdApplication, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating award application", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
    public ResponseEntity<Void> deleteApplication(@PathVariable Integer id, Principal principal) {
        Optional<StudentAwardApplication> optionalApplication = studentAwardApplicationService.findApplicationById(id);
        if (optionalApplication.isPresent()) {
            StudentAwardApplication application = optionalApplication.get();
            // 如果是学生角色，只能删除自己的申请
            if (principal != null && principal.getName() != null) {
                Optional<User> optionalUser = userService.findUserByUsername(principal.getName());
                if (optionalUser.isPresent() && "STUDENT".equals(optionalUser.get().getRole())) {
                    Optional<Student> optionalStudent = studentService.findStudentByUserId(optionalUser.get().getId());
                    if (optionalStudent.isPresent() && !optionalStudent.get().getId().equals(application.getStudentId())) {
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    }
                }
            }
            studentAwardApplicationService.deleteApplication(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * 根据ID查找申请
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<StudentAwardApplication> findApplicationById(@PathVariable Integer id, Principal principal) {
        Optional<StudentAwardApplication> optionalApplication = studentAwardApplicationService.findApplicationById(id);
        if (optionalApplication.isPresent()) {
            StudentAwardApplication application = optionalApplication.get();
            
            // 获取当前用户角色
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            
            // 如果是学生角色，只能查看自己的申请
            if ((role.equals("ROLE_STUDENT") || role.equals("STUDENT")) && principal != null) {
                Optional<User> optionalUser = userService.findUserByUsername(principal.getName());
                if (optionalUser.isPresent()) {
                    Optional<Student> optionalStudent = studentService.findStudentByUserId(optionalUser.get().getId());
                    if (optionalStudent.isPresent() && !optionalStudent.get().getId().equals(application.getStudentId())) {
                        // 学生试图查看其他学生的申请，返回403 Forbidden
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    }
                }
            }
            
            return new ResponseEntity<>(application, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * 根据学生ID查找申请
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<List<StudentAwardApplication>> findApplicationsByStudentId(@PathVariable Long studentId, Principal principal) {
        // 获取当前用户角色
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        
        // 如果是学生角色，只能查看自己的申请
        if ((role.equals("ROLE_STUDENT") || role.equals("STUDENT")) && principal != null) {
            Optional<User> optionalUser = userService.findUserByUsername(principal.getName());
            if (optionalUser.isPresent()) {
                Optional<Student> optionalStudent = studentService.findStudentByUserId(optionalUser.get().getId());
                if (optionalStudent.isPresent() && !optionalStudent.get().getId().equals(studentId)) {
                    // 学生试图查看其他学生的申请，返回403 Forbidden
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }
        }
        
        List<StudentAwardApplication> applications = studentAwardApplicationService.findApplicationsByStudentId(studentId);
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }
    
    /**
     * 根据学号查找申请
     */
    @GetMapping("/student/number/{studentNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<List<StudentAwardApplication>> findApplicationsByStudentNumber(@PathVariable String studentNumber, Principal principal) {
        // 获取当前用户角色
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        
        // 如果是学生角色，只能查看自己的申请
        if ((role.equals("ROLE_STUDENT") || role.equals("STUDENT")) && principal != null) {
            Optional<User> optionalUser = userService.findUserByUsername(principal.getName());
            if (optionalUser.isPresent()) {
                Optional<Student> optionalStudent = studentService.findStudentByUserId(optionalUser.get().getId());
                if (optionalStudent.isPresent() && !optionalStudent.get().getStudentNumber().equals(studentNumber)) {
                    // 学生试图查看其他学生的申请，返回403 Forbidden
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }
        }
        
        List<StudentAwardApplication> applications = studentAwardApplicationService.findApplicationsByStudentNumber(studentNumber);
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
     * 获取所有奖项申请
     * @return ResponseEntity 响应结果
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<StudentAwardApplication>> findAllApplications() {
        List<StudentAwardApplication> applications = studentAwardApplicationService.findAllApplications();
        return ResponseEntity.ok(applications);
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
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_STUDENT', 'TEACHER', 'ADMIN', 'STUDENT')")
    public ResponseEntity<IPage<StudentAwardApplication>> searchApplications(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer awardId,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) String awardName, 
            Principal principal) {
        Page<StudentAwardApplication> page = new Page<>(pageNum, pageSize);
        
        // 获取当前用户角色
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        
        // 如果是学生角色，只允许查看自己的申请
        if ((role.equals("ROLE_STUDENT") || role.equals("STUDENT")) && principal != null) {
            // 获取当前登录学生的学号
            Optional<User> optionalUser = userService.findUserByUsername(principal.getName());
            if (optionalUser.isPresent()) {
                // 通过用户ID获取学生信息
                Optional<Student> optionalStudent = studentService.findStudentByUserId(optionalUser.get().getId());
                if (optionalStudent.isPresent()) {
                    // 设置当前学生的学号，确保只能查看自己的申请
                    studentNumber = optionalStudent.get().getStudentNumber();
                }
            }
            // 学生只能查询自己的申请，忽略传入的studentName参数
            studentName = null;
        }
        
        IPage<StudentAwardApplication> applicationPage = 
            studentAwardApplicationService.findApplicationsByCondition(page, awardId, studentName, status, studentNumber, awardName);
        return ResponseEntity.ok(applicationPage);
    }

    /**
     * 检查学生是否已申请该奖项
     */
    @GetMapping("/check-exists")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Boolean> checkStudentApplicationExists(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String studentNumber,
            @RequestParam Integer awardId) {
        boolean exists;
        if (studentNumber != null) {
            exists = studentAwardApplicationService.checkStudentApplicationExistsByStudentNumber(studentNumber, awardId);
        } else if (studentId != null) {
            exists = studentAwardApplicationService.checkStudentApplicationExists(studentId, awardId);
        } else {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
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
    
    /**
     * 根据奖项ID获取申请总数
     */
    @GetMapping("/award/{awardId}/count")
    public ResponseEntity<Long> getApplicationCountByAwardId(@PathVariable Integer awardId) {
        try {
            long count = studentAwardApplicationService.countApplicationsByAwardId(awardId);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 根据奖项ID获取最终获奖数
     */
    @GetMapping("/award/{awardId}/approved-count")
    public ResponseEntity<Long> getApprovedCountByAwardId(@PathVariable Integer awardId) {
        try {
            long count = studentAwardApplicationService.countApprovedApplicationsByAwardId(awardId);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 根据奖项ID获取教师审核通过数
     */
    @GetMapping("/award/{awardId}/teacher-approved-count")
    public ResponseEntity<Long> getTeacherApprovedCountByAwardId(@PathVariable Integer awardId) {
        try {
            long count = studentAwardApplicationService.countTeacherApprovedApplicationsByAwardId(awardId);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 根据奖项ID获取管理员审核通过数
     */
    @GetMapping("/award/{awardId}/admin-approved-count")
    public ResponseEntity<Long> getAdminApprovedCountByAwardId(@PathVariable Integer awardId) {
        try {
            long count = studentAwardApplicationService.countAdminApprovedApplicationsByAwardId(awardId);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
