package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentselectionsystem.entity.Major;
import com.example.studentselectionsystem.entity.Student;
import com.example.studentselectionsystem.model.Award;
import com.example.studentselectionsystem.repository.MajorRepository;
import com.example.studentselectionsystem.repository.StudentRepository;
import com.example.studentselectionsystem.service.AwardService;
import com.example.studentselectionsystem.service.ScoreService;
import com.example.studentselectionsystem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 学生信息服务实现类
 */
@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private AwardService awardService;

    @Override
    public Student createStudent(Student student) {
        // 检查学号是否已存在
        if (existsByStudentNumber(student.getStudentNumber())) {
            throw new RuntimeException("学号已存在");
        }
        // 设置创建时间和更新时间
        student.setCreatedAt(new Date());
        student.setUpdatedAt(new Date());
        studentRepository.insert(student);
        return student;
    }

    @Override
    public List<Student> createStudents(List<Student> students) {
        // 批量创建学生
        for (Student student : students) {
            // 设置创建时间和更新时间
            student.setCreatedAt(new Date());
            student.setUpdatedAt(new Date());
            // 逐个插入学生
            studentRepository.insert(student);
        }
        return students;
    }

    @Override
    public Student updateStudent(Long id, Student student) {
        Optional<Student> optionalStudent = Optional.ofNullable(studentRepository.selectById(id));
        if (optionalStudent.isPresent()) {
            Student existingStudent = optionalStudent.get();
            // 更新学生信息
            existingStudent.setStudentNumber(student.getStudentNumber());
            existingStudent.setName(student.getName());
            existingStudent.setGender(student.getGender());
            existingStudent.setBirthDate(student.getBirthDate());
            existingStudent.setMajor(student.getMajor());
            existingStudent.setClassName(student.getClassName());
            existingStudent.setAdmissionYear(student.getAdmissionYear());
            existingStudent.setStatus(student.getStatus());
            existingStudent.setPhone(student.getPhone());
            existingStudent.setEmail(student.getEmail());
            existingStudent.setUserId(student.getUserId());
            existingStudent.setUpdatedAt(new Date());

            studentRepository.updateById(existingStudent);
            return existingStudent;
        }
        return null;
    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public void deleteStudents(List<Long> ids) {
        studentRepository.deleteBatchIds(ids);
    }

    @Override
    public Optional<Student> findStudentById(Long id) {
        return Optional.ofNullable(studentRepository.selectById(id));
    }

    @Override
    public Optional<Student> findStudentByStudentNumber(String studentNumber) {
        return studentRepository.selectByStudentId(studentNumber);
    }

    @Override
    public List<Student> findStudentsByName(String name) {
        return studentRepository.selectByName(name);
    }

    @Override
    public List<Student> findStudentsByNameContaining(String name) {
        return studentRepository.selectByNameContaining(name);
    }

    @Override
    public List<Student> findStudentsByMajorName(String majorName) {
        return studentRepository.selectByMajorName(majorName);
    }

    @Override
    public List<Student> findStudentsByGender(String gender) {
        return studentRepository.selectByGender(gender);
    }

    @Override
    public List<Student> findAllStudents() {
        return studentRepository.selectList(null);
    }

    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<Student> findStudentsByPage(com.baomidou.mybatisplus.core.metadata.IPage<Student> page) {
        return studentRepository.selectPage(page, null);
    }

    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<Student> findStudentsByPage(Integer current, Integer size) {
        // 创建MyBatis Plus分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Student> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        return studentRepository.selectPage(page, null);
    }

    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<Student> findStudentsByPageWithSearch(Integer page, Integer size, String studentNumber, String name, String className) {
        // 创建MyBatis Plus分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Student> pageObj = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        
        // 使用QueryWrapper构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Student> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        
        // 添加查询条件
        if (studentNumber != null && !studentNumber.isEmpty()) {
            queryWrapper.eq("student_number", studentNumber);
        }
        if (name != null && !name.isEmpty()) {
            queryWrapper.like("name", name);
        }
        if (className != null && !className.isEmpty()) {
            queryWrapper.like("class_name", className);
        }
        
        // 执行查询
        com.baomidou.mybatisplus.core.metadata.IPage<Student> result = studentRepository.selectPage(pageObj, queryWrapper);
        
        // 打印日志
        System.out.println("学生分页查询结果:");
        System.out.println("总记录数: " + result.getTotal());
        System.out.println("当前页: " + result.getCurrent());
        System.out.println("每页大小: " + result.getSize());
        System.out.println("总页数: " + result.getPages());
        System.out.println("记录列表: " + result.getRecords());
        
        return result;
    }

    @Override
    public Optional<Student> findStudentByIdWithMajor(Long id) {
        return studentRepository.selectByIdWithMajor(id);
    }

    @Override
    public List<Student> findAllStudentsWithMajor() {
        return studentRepository.selectAllWithMajor();
    }

    @Override
    public boolean existsByStudentNumber(String studentNumber) {
        return studentRepository.existsByStudentId(studentNumber);
    }

    @Override
    public Optional<Student> findStudentByUserId(Long userId) {
        return studentRepository.selectByUserId(userId);
    }



    @Override
    public long countStudents() {
        return studentRepository.selectCount(null);
    }

}