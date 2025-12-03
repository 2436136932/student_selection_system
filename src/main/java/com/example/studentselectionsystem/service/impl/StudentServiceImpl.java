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
            existingStudent.setUpdatedAt(new Date());

            // 更新专业信息
            existingStudent.setMajor(student.getMajor());

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
    public Optional<Student> findStudentByStudentId(String studentId) {
        return studentRepository.selectByStudentId(studentId);
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
    public List<Student> findStudentsByMajorId(Integer majorId) {
        return studentRepository.selectByMajorId(majorId);
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
    public Optional<Student> findStudentByIdWithMajor(Long id) {
        return studentRepository.selectByIdWithMajor(id);
    }

    @Override
    public List<Student> findAllStudentsWithMajor() {
        return studentRepository.selectAllWithMajor();
    }

    @Override
    public boolean existsByStudentId(String studentId) {
        return studentRepository.existsByStudentId(studentId);
    }

    @Override
    public List<Student> filterStudentsByScholarshipCriteria(double minAverageScore) {
        // 获取所有学生
        List<Student> allStudents = studentRepository.selectList(null);
        List<Student> eligibleStudents = new ArrayList<>();

        // 线性搜索：遍历每个学生，计算平均成绩并筛选
        for (Student student : allStudents) {
            Double averageScore = scoreService.getAverageScoreByStudentId(student.getId());
            // 如果平均成绩存在且大于等于最低要求，则符合条件
            if (averageScore != null && averageScore >= minAverageScore) {
                eligibleStudents.add(student);
            }
        }

        return eligibleStudents;
    }

    @Override
    public List<Student> filterStudentsByHonorCriteria(int minAwardCount, String awardLevel) {
        // 获取所有学生
        List<Student> allStudents = studentRepository.selectList(null);
        List<Student> eligibleStudents = new ArrayList<>();

        // 线性搜索：遍历每个学生，检查获奖情况
        for (Student student : allStudents) {
            List<Award> awards = awardService.getAwardsByStudentId(student.getStudentNumber());
            int count = 0;

            // 如果指定了奖项级别，则只计算该级别的奖项
            if (awardLevel != null && !awardLevel.isEmpty()) {
                for (Award award : awards) {
                    if (award.getAwardLevel().equals(awardLevel)) {
                        count++;
                    }
                }
            } else {
                // 否则计算所有奖项
                count = awards.size();
            }

            // 如果获奖次数大于等于最低要求，则符合条件
            if (count >= minAwardCount) {
                eligibleStudents.add(student);
            }
        }

        return eligibleStudents;
    }

    @Override
    public List<Student> filterStudentsByComprehensiveCriteria(Double minAverageScore, Integer minAwardCount, Integer majorId, Integer year) {
        // 获取所有学生
        List<Student> allStudents = studentRepository.selectList(null);
        List<Student> eligibleStudents = new ArrayList<>();

        // 线性搜索：遍历每个学生，综合检查所有条件
        for (Student student : allStudents) {
            boolean meetsCriteria = true;

            // 检查平均成绩条件
            if (minAverageScore != null) {
                Double averageScore = scoreService.getAverageScoreByStudentId(student.getId());
                if (averageScore == null || averageScore < minAverageScore) {
                    meetsCriteria = false;
                }
            }

            // 检查获奖次数条件
            if (meetsCriteria && minAwardCount != null) {
                int awardCount = awardService.getAwardCountByStudentId(student.getStudentNumber());
                if (awardCount < minAwardCount) {
                    meetsCriteria = false;
                }
            }

            // 检查专业条件 - 暂时注释，因为Student实体中的major是String类型
            // if (meetsCriteria && majorId != null) {
            //     // 这里需要根据实际的专业关联方式进行修改
            //     meetsCriteria = false;
            // }

            // 检查年级条件
            if (meetsCriteria && year != null) {
                // 假设学生的学号包含年级信息，例如：2021001
                String studentNumber = student.getStudentNumber();
                if (studentNumber.length() >= 4) {
                    try {
                        int studentYear = Integer.parseInt(studentNumber.substring(0, 4));
                        if (studentYear != year) {
                            meetsCriteria = false;
                        }
                    } catch (NumberFormatException e) {
                        // 如果学号无法解析为年份，则不符合条件
                        meetsCriteria = false;
                    }
                } else {
                    meetsCriteria = false;
                }
            }

            // 如果所有条件都满足，则加入结果列表
            if (meetsCriteria) {
                eligibleStudents.add(student);
            }
        }

        return eligibleStudents;
    }

}