package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentselectionsystem.entity.Student;
import com.example.studentselectionsystem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 学生信息控制器
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 创建学生
     */
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student createdStudent = studentService.createStudent(student);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }

    /**
     * 批量创建学生
     */
    @PostMapping("/batch")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<Student>> createStudents(@RequestBody List<Student> students) {
        List<Student> createdStudents = studentService.createStudents(students);
        return new ResponseEntity<>(createdStudents, HttpStatus.CREATED);
    }

    /**
     * 更新学生信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        Student updatedStudent = studentService.updateStudent(id, student);
        if (updatedStudent != null) {
            return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * 删除学生
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 批量删除学生
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteStudents(@RequestBody List<Long> ids) {
        studentService.deleteStudents(ids);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 根据ID查找学生
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('teacher')")
    public ResponseEntity<Student> findStudentById(@PathVariable Long id) {
        Optional<Student> optionalStudent = studentService.findStudentById(id);
        return optionalStudent.map(student -> new ResponseEntity<>(student, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 根据ID查找学生及其专业信息
     */
    @GetMapping("/{id}/with-major")
    @PreAuthorize("hasRole('admin') or hasRole('teacher')")
    public ResponseEntity<Student> findStudentByIdWithMajor(@PathVariable Long id) {
        Optional<Student> optionalStudent = studentService.findStudentByIdWithMajor(id);
        return optionalStudent.map(student -> new ResponseEntity<>(student, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 根据学号查找学生
     */
    @GetMapping("/student-id/{studentId}")
    @PreAuthorize("hasRole('admin') or hasRole('teacher') or authentication.principal.username == #studentId")
    public ResponseEntity<Student> findStudentByStudentId(@PathVariable String studentId) {
        Optional<Student> optionalStudent = studentService.findStudentByStudentId(studentId);
        return optionalStudent.map(student -> new ResponseEntity<>(student, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 根据姓名查找学生
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<List<Student>> findStudentsByName(@PathVariable String name) {
        List<Student> students = studentService.findStudentsByName(name);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    /**
     * 根据姓名模糊查找学生
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<Student>> searchStudentsByName(@RequestParam String name) {
        List<Student> students = studentService.findStudentsByNameContaining(name);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    /**
     * 根据专业ID查找学生
     */
    @GetMapping("/major/{majorId}")
    public ResponseEntity<List<Student>> findStudentsByMajorId(@PathVariable Integer majorId) {
        List<Student> students = studentService.findStudentsByMajorId(majorId);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    /**
     * 根据性别查找学生
     */
    @GetMapping("/gender/{gender}")
    public ResponseEntity<List<Student>> findStudentsByGender(@PathVariable String gender) {
        List<Student> students = studentService.findStudentsByGender(gender);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    /**
     * 获取所有学生
     */
    @GetMapping
    public ResponseEntity<List<Student>> findAllStudents() {
        List<Student> students = studentService.findAllStudents();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    /**
     * 获取所有学生及其专业信息
     */
    @GetMapping("/with-major")
    public ResponseEntity<List<Student>> findAllStudentsWithMajor() {
        List<Student> students = studentService.findAllStudentsWithMajor();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    /**
     * 分页获取学生
     */
    @GetMapping("/page")
    public ResponseEntity<IPage<Student>> findStudentsByPage(@RequestParam(defaultValue = "1") Integer current,
                                                              @RequestParam(defaultValue = "10") Integer size) {
        IPage<Student> students = studentService.findStudentsByPage(current, size);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    /**
     * 检查学号是否已存在
     */
    @GetMapping("/check-student-id/{studentId}")
    public ResponseEntity<Boolean> checkStudentIdExists(@PathVariable String studentId) {
        boolean exists = studentService.existsByStudentId(studentId);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

}