package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Score;
import com.example.studentselectionsystem.entity.Course;
import com.example.studentselectionsystem.entity.Student;
import com.example.studentselectionsystem.entity.User;
import com.example.studentselectionsystem.service.ScoreService;
import com.example.studentselectionsystem.service.CourseService;
import com.example.studentselectionsystem.service.StudentService;
import com.example.studentselectionsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;

/**
 * 成绩控制器
 */
@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取当前学生的学号（仅适用于学生角色）
     * @return 当前学生的学号，如果不是学生则返回null
     */
    private String getCurrentStudentNumber() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // 检查是否为学生角色
            boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT") || a.getAuthority().equals("STUDENT"));
            if (isStudent) {
                try {
                    String username = authentication.getName();
                    // 通过用户名查询用户信息
                    Optional<User> userOptional = userService.findUserByUsername(username);
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        // 通过用户ID查询学生信息
                        Optional<Student> studentOptional = studentService.findStudentByUserId(user.getId());
                        if (studentOptional.isPresent()) {
                            return studentOptional.get().getStudentNumber();
                        }
                    }
                } catch (Exception e) {
                    System.err.println("获取当前学生学号失败: " + e.getMessage());
                }
            }
        }
        return null;
    }
    
    /**
     * 获取当前学生的ID（仅适用于学生角色）
     * @return 当前学生的ID，如果不是学生则返回null
     */
    private Long getCurrentStudentId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // 检查是否为学生角色
            boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT") || a.getAuthority().equals("STUDENT"));
            if (isStudent) {
                try {
                    String username = authentication.getName();
                    // 通过用户名查询用户信息
                    Optional<User> userOptional = userService.findUserByUsername(username);
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        // 通过用户ID查询学生信息
                        Optional<Student> studentOptional = studentService.findStudentByUserId(user.getId());
                        if (studentOptional.isPresent()) {
                            return studentOptional.get().getId();
                        }
                    }
                } catch (Exception e) {
                    System.err.println("获取当前学生ID失败: " + e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * 创建成绩记录
     * @param score 成绩信息
     * @return 创建的成绩
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PostMapping
    public ResponseEntity<Score> createScore(@RequestBody Score score) {
        try {
            // 添加详细的日志记录
            System.out.println("\n接收到成绩创建请求:");
            System.out.println("学生ID: " + score.getStudentId());
            System.out.println("学生学号: " + score.getStudentNumber());
            System.out.println("课程代码: " + score.getCourseCode());
            System.out.println("课程ID: " + score.getCourseId());
            System.out.println("平时成绩: " + score.getUsualScore());
            System.out.println("考试成绩: " + score.getExamScore());
            System.out.println("总成绩: " + score.getTotalScore());
            System.out.println("学期: " + score.getSemester());
            
            // 如果前端传递了studentNumber，转换为studentId
            if (score.getStudentNumber() != null && !score.getStudentNumber().isEmpty()) {
                String studentNumber = score.getStudentNumber();
                System.out.println("正在根据学号查询学生: " + studentNumber);
                Student student = studentService.findStudentByStudentNumber(studentNumber).orElseThrow(() -> new RuntimeException("Student not found"));
                System.out.println("查询到的学生: ID=" + student.getId() + ", 姓名=" + student.getName());
                score.setStudentId(student.getId());
            } else if (score.getStudentId() != null) {
                // 如果直接提供了studentId，验证学生是否存在
                System.out.println("正在验证学生ID: " + score.getStudentId());
                Student student = studentService.findStudentById(score.getStudentId()).orElseThrow(() -> new RuntimeException("Student not found"));
                System.out.println("验证通过的学生: ID=" + student.getId() + ", 姓名=" + student.getName());
            }
            
            // 如果前端传递了courseCode，转换为courseId
            if (score.getCourseCode() != null && !score.getCourseCode().isEmpty()) {
                String courseCode = score.getCourseCode();
                System.out.println("正在根据课程代码查询课程: " + courseCode);
                Course course = courseService.findCourseByCourseCode(courseCode).orElseThrow(() -> new RuntimeException("Course not found"));
                System.out.println("查询到的课程: ID=" + course.getId() + ", 名称=" + course.getCourseName());
                score.setCourseId(course.getId());
            } else if (score.getCourseId() != null) {
                // 如果直接提供了courseId，验证课程是否存在
                System.out.println("正在验证课程ID: " + score.getCourseId());
                Course course = courseService.findCourseById(score.getCourseId()).orElseThrow(() -> new RuntimeException("Course not found"));
                System.out.println("验证通过的课程: ID=" + course.getId() + ", 名称=" + course.getCourseName());
            }
            
            // 准备创建成绩记录...
            Score createdScore = scoreService.createScore(score);
            System.out.println("成绩记录创建成功: ID=" + createdScore.getId());
            
            return new ResponseEntity<>(createdScore, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("创建成绩记录失败: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 更新成绩信息
     * @param id 成绩ID
     * @param score 成绩信息
     * @return 更新后的成绩
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PutMapping("/{id}")
    public ResponseEntity<Score> updateScore(@PathVariable("id") Long id, @RequestBody Score score) {
        try {
            // 如果前端传递了courseCode，转换为courseId
            if (score.getCourseCode() != null && !score.getCourseCode().isEmpty()) {
                String courseCode = score.getCourseCode();
                Course course = courseService.findCourseByCourseCode(courseCode).orElseThrow(() -> new RuntimeException("Course not found"));
                score.setCourseId(course.getId());
            }
            Score updatedScore = scoreService.updateScore(id, score);
            if (updatedScore != null) {
                return new ResponseEntity<>(updatedScore, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除成绩
     * @param id 成绩ID
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteScore(@PathVariable("id") Long id) {
        try {
            scoreService.deleteScore(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据ID查找成绩
     * @param id 成绩ID
     * @return 成绩信息
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<Score> findScoreById(@PathVariable("id") Long id) {
        Optional<Score> score = scoreService.findScoreById(id);
        if (score.isPresent()) {
            return new ResponseEntity<>(score.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 根据学生ID查找成绩
     * @param studentId 学生ID
     * @return 成绩列表
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Score>> findScoresByStudentId(@PathVariable("studentId") Long studentId) {
        try {
            // 如果是学生角色，验证是否访问自己的成绩
            Long currentStudentId = getCurrentStudentId();
            if (currentStudentId != null && !currentStudentId.equals(studentId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            List<Score> scores = scoreService.findScoresByStudentId(studentId);
            if (scores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(scores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据课程ID查找成绩
     * @param courseId 课程ID
     * @return 成绩列表
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Score>> findScoresByCourseId(@PathVariable("courseId") Long courseId) {
        try {
            List<Score> scores = scoreService.findScoresByCourseId(courseId);
            
            // 如果是学生角色，过滤为自己的成绩
            Long currentStudentId = getCurrentStudentId();
            if (currentStudentId != null) {
                scores = scores.stream()
                    .filter(score -> currentStudentId.equals(score.getStudentId()))
                    .collect(Collectors.toList());
            }
            
            if (scores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(scores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据学生ID和课程ID查找成绩
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 成绩信息
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<Score> findScoreByStudentIdAndCourseId(
            @PathVariable("studentId") Long studentId,
            @PathVariable("courseId") Long courseId) {
        // 如果是学生角色，验证是否访问自己的成绩
        Long currentStudentId = getCurrentStudentId();
        if (currentStudentId != null && !currentStudentId.equals(studentId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        
        Optional<Score> score = scoreService.findScoreByStudentIdAndCourseId(studentId, courseId);
        if (score.isPresent()) {
            return new ResponseEntity<>(score.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 根据学生ID和学期查找成绩
     * @param studentId 学生ID
     * @param semester 学期
     * @return 成绩列表
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/student/{studentId}/semester/{semester}")
    public ResponseEntity<List<Score>> findScoresByStudentIdAndSemester(
            @PathVariable("studentId") Long studentId,
            @PathVariable("semester") String semester) {
        try {
            // 如果是学生角色，验证是否访问自己的成绩
            Long currentStudentId = getCurrentStudentId();
            if (currentStudentId != null && !currentStudentId.equals(studentId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            List<Score> scores = scoreService.findScoresByStudentIdAndSemester(studentId, semester);
            if (scores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(scores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据课程ID和学期查找成绩
     * @param courseId 课程ID
     * @param semester 学期
     * @return 成绩列表
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/course/{courseId}/semester/{semester}")
    public ResponseEntity<List<Score>> findScoresByCourseIdAndSemester(
            @PathVariable("courseId") Long courseId,
            @PathVariable("semester") String semester) {
        try {
            List<Score> scores = scoreService.findScoresByCourseIdAndSemester(courseId, semester);
            
            // 如果是学生角色，过滤为自己的成绩
            Long currentStudentId = getCurrentStudentId();
            if (currentStudentId != null) {
                scores = scores.stream()
                    .filter(score -> currentStudentId.equals(score.getStudentId()))
                    .collect(Collectors.toList());
            }
            
            if (scores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(scores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据学期查找成绩
     * @param semester 学期
     * @return 成绩列表
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/semester/{semester}")
    public ResponseEntity<List<Score>> findScoresBySemester(@PathVariable("semester") String semester) {
        try {
            List<Score> scores = scoreService.findScoresBySemester(semester);
            
            // 如果是学生角色，过滤为自己的成绩
            Long currentStudentId = getCurrentStudentId();
            if (currentStudentId != null) {
                scores = scores.stream()
                    .filter(score -> currentStudentId.equals(score.getStudentId()))
                    .collect(Collectors.toList());
            }
            
            if (scores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(scores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取所有成绩
     * @return 成绩列表
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping
    public ResponseEntity<List<Score>> findAllScores() {
        // 添加日志记录
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ScoreController.class);
        logger.info("进入findAllScores方法");
        try {
            List<Score> scores = scoreService.findAllScores();
            
            // 如果是学生角色，过滤为自己的成绩
            Long currentStudentId = getCurrentStudentId();
            if (currentStudentId != null) {
                scores = scores.stream()
                    .filter(score -> currentStudentId.equals(score.getStudentId()))
                    .collect(Collectors.toList());
            }
            
            logger.info("findAllScores成功，获取到" + scores.size() + "条记录");
            return ResponseEntity.ok(scores);
        } catch (Throwable t) {
            logger.error("findAllScores方法发生异常", t);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 分页获取成绩
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @return 成绩分页列表
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/page")
    public ResponseEntity<IPage<Score>> findScoresByPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) String courseName) {
        try {
            System.out.println("=== findScoresByPage 方法开始执行 ===");
            System.out.println("参数: current=" + current + ", size=" + size + ", studentNumber=" + studentNumber + ", courseCode=" + courseCode + ", courseName=" + courseName);
            
            // 如果是学生角色，强制使用当前学生的学号
            String currentStudentNumber = getCurrentStudentNumber();
            if (currentStudentNumber != null) {
                System.out.println("学生角色，强制使用学号: " + currentStudentNumber);
                studentNumber = currentStudentNumber;
            }
            
            IPage<Score> scores = scoreService.findScoresByPage(current, size, studentNumber, courseCode, courseName);
            System.out.println("findScoresByPage 查询结果: " + scores.getTotal() + "条记录");
            System.out.println("=== findScoresByPage 方法执行完成 ===");
            
            return new ResponseEntity<>(scores, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("=== findScoresByPage 方法执行异常 ===");
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取学生的课程成绩总和
     * @param studentId 学生ID
     * @return 总成绩
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/student/{studentId}/total")
    public ResponseEntity<Double> getTotalScoreByStudentId(@PathVariable("studentId") Long studentId) {
        try {
            // 如果是学生角色，验证是否访问自己的成绩
            Long currentStudentId = getCurrentStudentId();
            if (currentStudentId != null && !currentStudentId.equals(studentId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            Double totalScore = scoreService.getTotalScoreByStudentId(studentId);
            return new ResponseEntity<>(totalScore, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取学生的课程成绩平均值
     * @param studentId 学生ID
     * @return 平均成绩
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/student/{studentId}/average")
    public ResponseEntity<Double> getAverageScoreByStudentId(@PathVariable("studentId") Long studentId) {
        try {
            // 如果是学生角色，验证是否访问自己的成绩
            Long currentStudentId = getCurrentStudentId();
            if (currentStudentId != null && !currentStudentId.equals(studentId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            Double averageScore = scoreService.getAverageScoreByStudentId(studentId);
            return new ResponseEntity<>(averageScore, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取学生的课程成绩总和（按学期）
     * @param studentId 学生ID
     * @param semester 学期
     * @return 总成绩
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/student/{studentId}/semester/{semester}/total")
    public ResponseEntity<Double> getTotalScoreByStudentIdAndSemester(
            @PathVariable("studentId") Long studentId,
            @PathVariable("semester") String semester) {
        try {
            // 如果是学生角色，验证是否访问自己的成绩
            Long currentStudentId = getCurrentStudentId();
            if (currentStudentId != null && !currentStudentId.equals(studentId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            Double totalScore = scoreService.getTotalScoreByStudentIdAndSemester(studentId, semester);
            return new ResponseEntity<>(totalScore, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取学生的课程成绩平均值（按学期）
     * @param studentId 学生ID
     * @param semester 学期
     * @return 平均成绩
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/student/{studentId}/semester/{semester}/average")
    public ResponseEntity<Double> getAverageScoreByStudentIdAndSemester(
            @PathVariable("studentId") Long studentId,
            @PathVariable("semester") String semester) {
        try {
            // 如果是学生角色，验证是否访问自己的成绩
            Long currentStudentId = getCurrentStudentId();
            if (currentStudentId != null && !currentStudentId.equals(studentId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            Double averageScore = scoreService.getAverageScoreByStudentIdAndSemester(studentId, semester);
            return new ResponseEntity<>(averageScore, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取课程的平均成绩
     * @param courseId 课程ID
     * @return 平均成绩
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/course/{courseId}/average")
    public ResponseEntity<Double> getAverageScoreByCourseId(@PathVariable("courseId") Long courseId) {
        try {
            // 如果是学生角色，检查该课程是否包含当前学生的成绩
            Long currentStudentId = getCurrentStudentId();
            if (currentStudentId != null) {
                Optional<Score> studentScore = scoreService.findScoreByStudentIdAndCourseId(currentStudentId, courseId);
                if (!studentScore.isPresent()) {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }
            
            Double averageScore = scoreService.getAverageScoreByCourseId(courseId);
            return new ResponseEntity<>(averageScore, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取课程的平均成绩（按学期）
     * @param courseId 课程ID
     * @param semester 学期
     * @return 平均成绩
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/course/{courseId}/semester/{semester}/average")
    public ResponseEntity<Double> getAverageScoreByCourseIdAndSemester(
            @PathVariable("courseId") Long courseId,
            @PathVariable("semester") String semester) {
        try {
            // 如果是学生角色，检查该课程是否包含当前学生的成绩
            Long currentStudentId = getCurrentStudentId();
            if (currentStudentId != null) {
                Optional<Score> studentScore = scoreService.findScoreByStudentIdAndCourseId(currentStudentId, courseId);
                if (!studentScore.isPresent()) {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }
            
            Double averageScore = scoreService.getAverageScoreByCourseIdAndSemester(courseId, semester);
            return new ResponseEntity<>(averageScore, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 使用冒泡排序算法按成绩降序排序
     * @return 排序后的成绩列表
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/sorted/desc")
    public ResponseEntity<List<Score>> bubbleSortByScoreDesc() {
        try {
            List<Score> scores = scoreService.findAllScores();
            
            // 如果是学生角色，过滤为自己的成绩
            Long currentStudentId = getCurrentStudentId();
            if (currentStudentId != null) {
                scores = scores.stream()
                    .filter(score -> currentStudentId.equals(score.getStudentId()))
                    .collect(Collectors.toList());
            }
            
            List<Score> sortedScores = scoreService.bubbleSortByScoreDesc(scores);
            if (sortedScores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(sortedScores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 使用冒泡排序算法按成绩升序排序
     * @return 排序后的成绩列表
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/sorted/asc")
    public ResponseEntity<List<Score>> bubbleSortByScoreAsc() {
        try {
            List<Score> scores = scoreService.findAllScores();
            
            // 如果是学生角色，过滤为自己的成绩
            Long currentStudentId = getCurrentStudentId();
            if (currentStudentId != null) {
                scores = scores.stream()
                    .filter(score -> currentStudentId.equals(score.getStudentId()))
                    .collect(Collectors.toList());
            }
            
            List<Score> sortedScores = scoreService.bubbleSortByScoreAsc(scores);
            if (sortedScores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(sortedScores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取成绩统计数据
     * @param studentNumber 学生学号（可选）
     * @param courseCode 课程代码（可选）
     * @param semester 学期（可选）
     * @return 统计数据
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/statistics")
    public ResponseEntity<java.util.Map<String, Object>> getScoreStatistics(
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) String semester) {
        try {
            // 如果是学生角色，强制使用当前学生的学号
            String currentStudentNumber = getCurrentStudentNumber();
            if (currentStudentNumber != null) {
                studentNumber = currentStudentNumber;
            }
            
            java.util.Map<String, Object> statistics = scoreService.getScoreStatistics(studentNumber, courseCode, semester);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}