package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Course;
import com.example.studentselectionsystem.entity.Major;
import com.example.studentselectionsystem.entity.Score;
import com.example.studentselectionsystem.entity.Student;
import com.example.studentselectionsystem.service.CourseService;
import com.example.studentselectionsystem.service.MajorService;
import com.example.studentselectionsystem.service.ScoreService;
import com.example.studentselectionsystem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 课程控制器
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;
    
    @Autowired
    private ScoreService scoreService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private MajorService majorService;

    /**
     * 创建课程
     * @param course 课程信息
     * @return 创建的课程
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        try {
            Course createdCourse = courseService.createCourse(course);
            return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 更新课程信息
     * @param id 课程ID
     * @param course 课程信息
     * @return 更新后的课程
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Course> updateCourse(@PathVariable("id") Long id, @RequestBody Course course) {
        try {
            Course updatedCourse = courseService.updateCourse(id, course);
            if (updatedCourse != null) {
                return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除课程
     * @param id 课程ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<HttpStatus> deleteCourse(@PathVariable("id") Long id) {
        try {
            courseService.deleteCourse(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据ID查找课程
     * @param id 课程ID
     * @return 课程信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<Course> findCourseById(@PathVariable("id") Long id) {
        Optional<Course> course = courseService.findCourseById(id);
        if (course.isPresent()) {
            return new ResponseEntity<>(course.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 根据课程名称查找课程
     * @param courseName 课程名称
     * @return 课程信息
     */
    @GetMapping("/name/{courseName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<Course> findCourseByCourseName(@PathVariable("courseName") String courseName) {
        Optional<Course> course = courseService.findCourseByCourseName(courseName);
        if (course.isPresent()) {
            return new ResponseEntity<>(course.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 根据课程代码查找课程
     * @param courseCode 课程代码
     * @return 课程信息
     */
    @GetMapping("/code/{courseCode}")
    public ResponseEntity<Course> findCourseByCourseCode(@PathVariable("courseCode") String courseCode) {
        Optional<Course> course = courseService.findCourseByCourseCode(courseCode);
        if (course.isPresent()) {
            return new ResponseEntity<>(course.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 获取所有课程
     * @return 课程列表
     */
    @GetMapping
    public ResponseEntity<List<Course>> findAllCourses() {
        try {
            List<Course> courses = courseService.findAllCourses();
            if (courses.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取所有课程（包含教师信息）
     * @return 课程列表
     */
    @GetMapping("/with-teacher")
    public ResponseEntity<List<Course>> findAllCoursesWithTeacher() {
        try {
            List<Course> courses = courseService.findAllCoursesWithTeacher();
            if (courses.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 分页获取课程
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @param courseCode 课程代码
     * @param courseName 课程名称
     * @param teacherName 教师名称
     * @return 课程分页列表
     */
    @GetMapping("/page")
    public ResponseEntity<IPage<Course>> findCoursesByPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String teacherName) {
        try {
            IPage<Course> courses = courseService.findCoursesByPage(current, size, courseCode, courseName, teacherName);
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 判断课程名称是否存在
     * @param courseName 课程名称
     * @return 是否存在
     */
    @GetMapping("/exists/name/{courseName}")
    public ResponseEntity<Boolean> existsByCourseName(@PathVariable("courseName") String courseName) {
        try {
            boolean exists = courseService.existsByCourseName(courseName);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 判断课程代码是否存在
     * @param courseCode 课程代码
     * @return 是否存在
     */
    @GetMapping("/exists/code/{courseCode}")
    public ResponseEntity<Boolean> existsByCourseCode(@PathVariable("courseCode") String courseCode) {
        try {
            boolean exists = courseService.existsByCourseCode(courseCode);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取课程的所有学生
     * @param id 课程ID
     * @return 学生列表
     */
    @GetMapping("/{id}/students")
    public ResponseEntity<List<Map<String, Object>>> getCourseStudents(@PathVariable("id") Long id) {
        try {
            Optional<Course> course = courseService.findCourseById(id);
            if (!course.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            // 获取课程的所有成绩记录
            List<Score> scores = scoreService.findScoresByCourseId(id);
            List<Map<String, Object>> students = new ArrayList<>();
            
            // 提取学生信息
            for (Score score : scores) {
                Optional<Student> student = studentService.findStudentById(score.getStudentId());
                if (student.isPresent()) {
                    Map<String, Object> studentInfo = new HashMap<>();
                    studentInfo.put("id", student.get().getId());
                    studentInfo.put("studentNumber", student.get().getStudentNumber());
                    studentInfo.put("name", student.get().getName());
                    studentInfo.put("gender", student.get().getGender());
                    studentInfo.put("className", student.get().getClassName());
                    studentInfo.put("major", student.get().getMajor());
                    // 从专业表中获取正确的院系信息
                    String department = "";
                    if (student.get().getMajor() != null) {
                        Optional<Major> major = majorService.findMajorByName(student.get().getMajor());
                        if (major.isPresent()) {
                            department = major.get().getDepartment();
                        } else {
                            // 如果找不到对应的专业记录，使用专业名称作为院系信息
                            department = student.get().getMajor();
                        }
                    }
                    studentInfo.put("department", department);
                    students.add(studentInfo);
                }
            }
            
            return new ResponseEntity<>(students, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 更新课程的学生列表
     * @param id 课程ID
     * @param request 请求体，包含studentIds字段
     * @return 更新结果
     */
    @PutMapping("/{id}/students")
    public ResponseEntity<Map<String, Object>> updateCourseStudents(@PathVariable("id") Long id, @RequestBody Map<String, List<Long>> request) {
        try {
            Optional<Course> course = courseService.findCourseById(id);
            if (!course.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            // 获取学生ID列表
            List<Long> studentIds = request.getOrDefault("studentIds", new ArrayList<>());
            
            // 删除课程的所有现有成绩记录
            List<Score> existingScores = scoreService.findScoresByCourseId(id);
            for (Score score : existingScores) {
                scoreService.deleteScore(score.getId());
            }
            
            // 为新的学生列表创建成绩记录
            List<Score> newScores = new ArrayList<>();
            for (Long studentId : studentIds) {
                Optional<Student> student = studentService.findStudentById(studentId);
                if (student.isPresent()) {
                    Score score = new Score();
                    score.setStudentId(studentId);
                    score.setCourseId(id);
                    score.setUsualScore(BigDecimal.ZERO);
                    score.setExamScore(BigDecimal.ZERO);
                    score.setTotalScore(BigDecimal.ZERO);
                    score.setStatus(0); // 未发布
                    score.setCreatedAt(LocalDateTime.now());
                    score.setUpdatedAt(LocalDateTime.now());
                    newScores.add(scoreService.createScore(score));
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "课程学生更新成功");
            response.put("count", newScores.size());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "课程学生更新失败: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}