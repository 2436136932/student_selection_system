package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentselectionsystem.entity.StudentAwardApplication;
import com.example.studentselectionsystem.service.StudentAwardApplicationService;
import com.example.studentselectionsystem.service.UserService;
import com.example.studentselectionsystem.service.AwardService;
import com.example.studentselectionsystem.entity.User;
import com.example.studentselectionsystem.entity.Student;
import com.example.studentselectionsystem.entity.Teacher;
import com.example.studentselectionsystem.entity.Award;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.time.Instant;
import java.util.UUID;

/**
 * 学生奖项申请控制器
 */
@RestController
@RequestMapping("/api/student-award-applications")
@CrossOrigin(origins = "http://localhost:5173") // 允许来自前端的跨域请求
public class StudentAwardApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(StudentAwardApplicationController.class);

    @Autowired
    private StudentAwardApplicationService studentAwardApplicationService;
    
    @Autowired
    private com.example.studentselectionsystem.service.StudentService studentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private com.example.studentselectionsystem.service.TeacherService teacherService;
    
    @Autowired
    private AwardService awardService;

    // 文件存储根路径，从配置文件读取
    @Value("${file.storage.dir}")
    private String STORAGE_DIR;
    
    // 允许的文件扩展名，从配置文件读取
    @Value("${file.storage.allowed-extensions}")
    private String allowedExtensions;

    /**
     * 上传申请材料
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> uploadMaterial(@RequestParam("file") MultipartFile file, 
                                                             Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 1. 验证文件格式
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase() : "";
            List<String> allowedExtensionsList = Arrays.asList(this.allowedExtensions.split(","));
            
            if (!allowedExtensionsList.contains(fileExtension)) {
                response.put("success", false);
                response.put("message", "不支持的文件格式，请上传Word、PDF、PPT或图片文件");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            // 2. 验证文件大小（最大10MB）
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (file.getSize() > maxSize) {
                response.put("success", false);
                response.put("message", "文件大小超过限制，最大支持10MB");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            // 3. 验证用户是否为学生
            Optional<User> optionalUser = userService.findUserByUsername(principal.getName());
            if (!optionalUser.isPresent()) {
                response.put("success", false);
                response.put("message", "用户不存在");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
            Optional<Student> optionalStudent = studentService.findStudentByUserId(optionalUser.get().getId());
            if (!optionalStudent.isPresent()) {
                response.put("success", false);
                response.put("message", "当前用户不是学生");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            
            // 4. 生成唯一文件名
            String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;
            Path filePath = Paths.get(STORAGE_DIR, uniqueFilename);
            
            // 添加日志，打印实际的存储路径，便于调试
            logger.info("当前STORAGE_DIR: {}", STORAGE_DIR);
            logger.info("即将创建的文件路径: {}", filePath);
            logger.info("父目录路径: {}", filePath.getParent());
            
            // 确保存储目录存在，添加更可靠的错误处理
            try {
                Path parentDir = filePath.getParent();
                if (parentDir != null && !Files.exists(parentDir)) {
                    logger.info("父目录不存在，正在创建: {}", parentDir);
                    Files.createDirectories(parentDir);
                    logger.info("父目录创建成功: {}", parentDir);
                } else {
                    logger.info("父目录已存在: {}", parentDir);
                }
            } catch (Exception e) {
                logger.error("创建目录失败: {}", e.getMessage(), e);
                response.put("success", false);
                response.put("message", "文件上传失败: 创建存储目录失败 - " + e.getMessage());
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
            // 5. 保存文件
            logger.info("开始保存文件到: {}", filePath);
            file.transferTo(filePath.toFile());
            logger.info("文件保存成功: {}", filePath);
            
            // 6. 返回成功信息
            response.put("success", true);
            response.put("message", "文件上传成功");
            response.put("materialPath", uniqueFilename);
            response.put("materialName", originalFilename);
            response.put("materialSize", file.getSize());
            response.put("materialType", file.getContentType());
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("文件上传失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "文件上传失败: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 下载申请材料
     */
    @GetMapping("/download/{applicationId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<Resource> downloadMaterial(@PathVariable Long applicationId, Principal principal) {
        try {
            // 1. 查找申请
            Optional<StudentAwardApplication> optionalApplication = studentAwardApplicationService.findApplicationById(applicationId);
            if (!optionalApplication.isPresent()) {
                logger.error("申请不存在，ID: {}", applicationId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            StudentAwardApplication application = optionalApplication.get();
            
            // 2. 验证用户权限（复用findApplicationById方法的权限逻辑）
            ResponseEntity<StudentAwardApplication> permissionCheck = findApplicationById(applicationId, principal);
            if (permissionCheck.getStatusCode() != HttpStatus.OK) {
                logger.error("用户权限不足，无法下载申请ID: {}", applicationId);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            // 3. 检查是否有材料
            if (application.getMaterialPath() == null) {
                logger.error("申请ID: {} 未上传材料", applicationId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            // 4. 构建文件路径，添加详细日志
            logger.info("下载材料 - STORAGE_DIR: {}", STORAGE_DIR);
            logger.info("下载材料 - 数据库中的材料路径: {}", application.getMaterialPath());
            
            Path filePath = Paths.get(STORAGE_DIR, application.getMaterialPath());
            logger.info("下载材料 - 完整文件路径: {}", filePath);
            
            Resource resource = new UrlResource(filePath.toUri());
            
            // 增强文件存在性检查
            if (!resource.exists()) {
                logger.error("文件不存在: {}", filePath);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            if (!resource.isReadable()) {
                logger.error("文件不可读: {}", filePath);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            // 5. 处理文件类型和大小
            String contentType = application.getMaterialType();
            if (contentType == null || contentType.isEmpty()) {
                logger.info("材料类型为空，根据文件名推断: {}", application.getMaterialName());
                contentType = getMimeTypeByExtension(application.getMaterialName());
                logger.info("推断出的MIME类型: {}", contentType);
            }
            
            long contentLength = application.getMaterialSize() != null ? application.getMaterialSize() : Files.size(filePath);
            
            logger.info("文件下载成功，路径: {}", filePath);
            logger.info("文件类型: {}, 文件大小: {}", contentType, contentLength);
            
            // 6. 返回文件
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + application.getMaterialName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                    .body(resource);
            
        } catch (Exception e) {
            logger.error("文件下载失败: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 根据文件扩展名获取MIME类型
     */
    private String getMimeTypeByExtension(String fileName) {
        if (fileName == null) {
            return "application/octet-stream";
        }
        
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 预览申请材料
     */
    @GetMapping("/preview/{applicationId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<Resource> previewMaterial(@PathVariable Long applicationId, Principal principal) {
        try {
            // 1. 查找申请
            Optional<StudentAwardApplication> optionalApplication = studentAwardApplicationService.findApplicationById(applicationId);
            if (!optionalApplication.isPresent()) {
                logger.error("申请不存在，ID: {}", applicationId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            StudentAwardApplication application = optionalApplication.get();
            
            // 2. 验证用户权限（复用findApplicationById方法的权限逻辑）
            ResponseEntity<StudentAwardApplication> permissionCheck = findApplicationById(applicationId, principal);
            if (permissionCheck.getStatusCode() != HttpStatus.OK) {
                logger.error("用户权限不足，无法预览申请ID: {}", applicationId);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            // 3. 检查是否有材料
            if (application.getMaterialPath() == null) {
                logger.error("申请ID: {} 未上传材料", applicationId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            // 4. 构建文件路径，添加详细日志
            logger.info("预览材料 - STORAGE_DIR: {}", STORAGE_DIR);
            logger.info("预览材料 - 数据库中的材料路径: {}", application.getMaterialPath());
            
            Path filePath = Paths.get(STORAGE_DIR, application.getMaterialPath());
            logger.info("预览材料 - 完整文件路径: {}", filePath);
            
            Resource resource = new UrlResource(filePath.toUri());
            
            // 增强文件存在性检查
            if (!resource.exists()) {
                logger.error("文件不存在: {}", filePath);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            if (!resource.isReadable()) {
                logger.error("文件不可读: {}", filePath);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            // 5. 处理文件类型和大小
            String contentType = application.getMaterialType();
            if (contentType == null || contentType.isEmpty()) {
                logger.info("材料类型为空，根据文件名推断: {}", application.getMaterialName());
                contentType = getMimeTypeByExtension(application.getMaterialName());
                logger.info("推断出的MIME类型: {}", contentType);
            }
            
            long contentLength = application.getMaterialSize() != null ? application.getMaterialSize() : Files.size(filePath);
            
            logger.info("文件预览成功，路径: {}", filePath);
            logger.info("文件类型: {}, 文件大小: {}", contentType, contentLength);
            
            // 6. 返回文件，使用inline方式支持浏览器预览
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + application.getMaterialName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                    .body(resource);
            
        } catch (Exception e) {
            logger.error("文件预览失败: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 创建学生奖项申请
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentAwardApplication> createApplication(@RequestBody StudentAwardApplication application, Principal principal) {
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
    }

    /**
     * 更新学生奖项申请
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentAwardApplication> updateApplication(
            @PathVariable Long id, 
            @RequestBody StudentAwardApplication application, 
            Principal principal) {
        try {
            // 检查申请是否存在
            Optional<StudentAwardApplication> optionalApplication = studentAwardApplicationService.findApplicationById(id);
            if (!optionalApplication.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            StudentAwardApplication existingApplication = optionalApplication.get();
            
            // 验证学生权限
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            if ((role.equals("ROLE_STUDENT") || role.equals("STUDENT")) && principal != null) {
                Optional<User> optionalUser = userService.findUserByUsername(principal.getName());
                if (optionalUser.isPresent()) {
                    Optional<Student> optionalStudent = studentService.findStudentByUserId(optionalUser.get().getId());
                    if (optionalStudent.isPresent() && !optionalStudent.get().getId().equals(existingApplication.getStudentId())) {
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    }
                }
            }
            
            // 设置申请ID
            application.setId(id);
            // 确保学生ID不变
            application.setStudentId(existingApplication.getStudentId());
            
            // 更新申请
            StudentAwardApplication updatedApplication = studentAwardApplicationService.updateApplication(application);
            logger.info("Updated award application: {}", updatedApplication);
            return new ResponseEntity<>(updatedApplication, HttpStatus.OK);
            
        } catch (ResponseStatusException e) {
            logger.error("更新申请失败: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("更新申请失败: {}", e.getMessage(), e);
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
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id, Principal principal) {
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
    public ResponseEntity<StudentAwardApplication> findApplicationById(@PathVariable Long id, Principal principal) {
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
            // 如果是教师角色，只能查看自己负责审批的申请
            else if ((role.equals("ROLE_TEACHER") || role.equals("TEACHER")) && principal != null) {
                Optional<User> optionalUser = userService.findUserByUsername(principal.getName());
                if (optionalUser.isPresent()) {
                    Optional<Teacher> optionalTeacher = teacherService.findTeacherByUserId(optionalUser.get().getId());
                    if (optionalTeacher.isPresent()) {
                        Long currentTeacherId = optionalTeacher.get().getId();
                        // 查询申请对应的奖项信息
                        Optional<com.example.studentselectionsystem.model.Award> awardOptional = awardService.getAwardById(String.valueOf(application.getAwardId()));
                        if (!awardOptional.isPresent() || awardOptional.get().getApprovingTeacherId() == null || !awardOptional.get().getApprovingTeacherId().equals(currentTeacherId)) {
                            // 教师试图查看非自己负责审批的申请，返回403 Forbidden
                            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                        }
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
    public ResponseEntity<List<StudentAwardApplication>> findApplicationsByAwardId(@PathVariable Long awardId) {
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
            @RequestParam(required = false) Long awardId,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) String awardName, 
            Principal principal) {
        Page<StudentAwardApplication> page = new Page<>(pageNum, pageSize);
        
        // 获取当前用户角色
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        Long currentTeacherId = null;
        
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
        } else if ((role.equals("ROLE_TEACHER") || role.equals("TEACHER")) && principal != null) {
            // 如果是教师角色，获取当前登录教师的ID
            Optional<User> optionalUser = userService.findUserByUsername(principal.getName());
            if (optionalUser.isPresent()) {
                // 通过用户ID获取教师信息
                Optional<Teacher> optionalTeacher = teacherService.findTeacherByUserId(optionalUser.get().getId());
                if (optionalTeacher.isPresent()) {
                    // 设置当前教师的ID，用于过滤申请
                    currentTeacherId = optionalTeacher.get().getId();
                }
            }
        }
        
        IPage<StudentAwardApplication> applicationPage = 
            studentAwardApplicationService.findApplicationsByCondition(page, awardId, studentName, status, studentNumber, awardName, currentTeacherId);
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
            @RequestParam Long awardId) {
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
    public ResponseEntity<Long> getApplicationCountByAwardId(@PathVariable Long awardId) {
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
    public ResponseEntity<Long> getApprovedCountByAwardId(@PathVariable Long awardId) {
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
    public ResponseEntity<Long> getTeacherApprovedCountByAwardId(@PathVariable Long awardId) {
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
    public ResponseEntity<Long> getAdminApprovedCountByAwardId(@PathVariable Long awardId) {
        try {
            long count = studentAwardApplicationService.countAdminApprovedApplicationsByAwardId(awardId);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
