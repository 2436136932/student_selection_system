package com.example.studentselectionsystem.util;

import com.example.studentselectionsystem.entity.Course;
import com.example.studentselectionsystem.entity.Student;
import com.example.studentselectionsystem.repository.CourseRepository;
import com.example.studentselectionsystem.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据库检查工具类
 */
@Component
public class DatabaseChecker implements CommandLineRunner {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== 数据库数据检查 ===");

        // 检查课程ID为1的课程
        Course course = courseRepository.selectById(1L);
        if (course != null) {
            System.out.println("课程ID为1的课程存在: " + course.getCourseName() + " (" + course.getCourseCode() + ")");
        } else {
            System.out.println("课程ID为1的课程不存在！");
        }

        // 检查学生ID为1的学生
        Student student = studentRepository.selectById(1L);
        if (student != null) {
            System.out.println("学生ID为1的学生存在: " + student.getName() + " (" + student.getStudentNumber() + ")");
        } else {
            System.out.println("学生ID为1的学生不存在！");
        }

        // 检查所有课程
        System.out.println("\n=== 所有课程 ===");
        courseRepository.selectList(null).forEach(c -> {
            System.out.println("ID: " + c.getId() + ", 名称: " + c.getCourseName() + ", 代码: " + c.getCourseCode());
        });

        // 检查所有学生
        System.out.println("\n=== 所有学生 ===");
        studentRepository.selectList(null).forEach(s -> {
            System.out.println("ID: " + s.getId() + ", 姓名: " + s.getName() + ", 学号: " + s.getStudentNumber());
        });
    }
}
