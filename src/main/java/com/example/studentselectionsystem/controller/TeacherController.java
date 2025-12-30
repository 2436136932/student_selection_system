package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Course;
import com.example.studentselectionsystem.entity.Teacher;
import com.example.studentselectionsystem.service.CourseService;
import com.example.studentselectionsystem.service.TeacherService;
import com.example.studentselectionsystem.util.PageResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 教师信息控制器
 */
@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CourseService courseService;

    /**
     * 创建教师
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createTeacher(@RequestBody Teacher teacher) {
        try {
            Teacher createdTeacher = teacherService.createTeacher(teacher);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", createdTeacher);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 批量创建教师
     */
    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createTeachers(@RequestBody List<Teacher> teachers) {
        try {
            List<Teacher> createdTeachers = teacherService.createTeachers(teachers);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", createdTeachers);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 删除教师
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 批量删除教师
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTeachers(@RequestBody List<Long> ids) {
        teacherService.deleteTeachers(ids);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 根据ID查找教师
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Teacher> findTeacherById(@PathVariable Long id) {
        Optional<Teacher> optionalTeacher = teacherService.findTeacherById(id);
        return optionalTeacher.map(teacher -> new ResponseEntity<>(teacher, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * 根据用户ID查找教师
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Teacher> findTeacherByUserId(@PathVariable Long userId) {
        Optional<Teacher> optionalTeacher = teacherService.findTeacherByUserId(userId);
        return optionalTeacher.map(teacher -> new ResponseEntity<>(teacher, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 根据工号查找教师
     */
    @GetMapping("/number/{teacherNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Teacher> findTeacherByTeacherNumber(@PathVariable String teacherNumber) {
        Optional<Teacher> optionalTeacher = teacherService.findTeacherByTeacherNumber(teacherNumber);
        return optionalTeacher.map(teacher -> new ResponseEntity<>(teacher, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 根据姓名查找教师
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<List<Teacher>> findTeachersByName(@PathVariable String name) {
        List<Teacher> teachers = teacherService.findTeachersByName(name);
        return new ResponseEntity<>(teachers, HttpStatus.OK);
    }

    /**
     * 根据姓名模糊查找教师
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<Teacher>> searchTeachersByName(@RequestParam String name) {
        List<Teacher> teachers = teacherService.findTeachersByNameContaining(name);
        return new ResponseEntity<>(teachers, HttpStatus.OK);
    }

    /**
     * 根据部门查找教师
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Teacher>> findTeachersByDepartment(@PathVariable String department) {
        List<Teacher> teachers = teacherService.findTeachersByDepartment(department);
        return new ResponseEntity<>(teachers, HttpStatus.OK);
    }

    /**
     * 获取所有教师（支持分页和搜索）
     */
    @GetMapping
    public ResponseEntity<IPage<Teacher>> findAllTeachers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String teacherNumber,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department) {
        IPage<Teacher> teachers = teacherService.findTeachersByPageWithSearch(page, size, teacherNumber, name, department);
        return new ResponseEntity<>(teachers, HttpStatus.OK);
    }

    /**
     * 分页获取教师
     */
    @GetMapping("/page")
    public ResponseEntity<?> findTeachersByPage(@RequestParam(defaultValue = "1") Integer current,
                                                              @RequestParam(defaultValue = "10") Integer size) {
        IPage<Teacher> teachers = teacherService.findTeachersByPage(current, size);
        return ResponseEntity.ok(PageResultUtil.convertToPageResult(teachers));
    }

    /**
     * 检查工号是否已存在
     */
    @GetMapping("/check-teacher-number/{teacherNumber}")
    public ResponseEntity<Boolean> checkTeacherNumberExists(@PathVariable String teacherNumber) {
        boolean exists = teacherService.existsByTeacherNumber(teacherNumber);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    /**
     * 获取教师总数
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTeacherCount() {
        try {
            long count = teacherService.countTeachers();
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取所有教师（用于下拉选择）
     */
    @GetMapping("/all")
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        List<Teacher> teachers = teacherService.findAllTeachers();
        return new ResponseEntity<>(teachers, HttpStatus.OK);
    }

    /**
     * 更新教师信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Map<String, Object>> updateTeacher(@PathVariable Long id, @RequestBody Teacher teacher) {
        try {
            Teacher updatedTeacher = teacherService.updateTeacher(id, teacher);
            if (updatedTeacher != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", updatedTeacher);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "教师不存在");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 获取教师教授的课程
     */
    @GetMapping("/{id}/courses")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<IPage<Course>> getTeacherCourses(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            // 直接根据教师ID查找教师信息
            Optional<Teacher> optionalTeacher = teacherService.findTeacherById(id);
            if (optionalTeacher.isPresent()) {
                // 使用教师ID查询课程
                IPage<Course> courses = courseService.findCoursesByTeacherId(id, current, size);
                return new ResponseEntity<>(courses, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
