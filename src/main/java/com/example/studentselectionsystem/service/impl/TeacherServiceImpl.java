package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentselectionsystem.entity.Teacher;
import com.example.studentselectionsystem.mapper.TeacherMapper;
import com.example.studentselectionsystem.repository.TeacherRepository;
import com.example.studentselectionsystem.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 教师信息服务实现类
 */
@Service
@Transactional
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public Teacher createTeacher(Teacher teacher) {
        // 检查工号是否已存在
        if (existsByTeacherNumber(teacher.getTeacherNumber())) {
            throw new RuntimeException("工号已存在");
        }
        // 设置创建时间和更新时间
        teacher.setCreatedAt(new Date());
        teacher.setUpdatedAt(new Date());
        teacherRepository.insert(teacher);
        return teacher;
    }

    @Override
    public List<Teacher> createTeachers(List<Teacher> teachers) {
        // 检查工号是否已存在
        for (Teacher teacher : teachers) {
            if (existsByTeacherNumber(teacher.getTeacherNumber())) {
                throw new RuntimeException("工号 " + teacher.getTeacherNumber() + " 已存在");
            }
            // 设置创建时间和更新时间
            teacher.setCreatedAt(new Date());
            teacher.setUpdatedAt(new Date());
            // 逐个插入教师
            teacherRepository.insert(teacher);
        }
        return teachers;
    }

    @Override
    public Teacher updateTeacher(Long id, Teacher teacher) {
        teacher.setId(id);
        updateById(teacher);
        return teacher;
    }

    @Override
    public void deleteTeacher(Long id) {
        removeById(id);
    }

    @Override
    public void deleteTeachers(List<Long> ids) {
        removeByIds(ids);
    }

    @Override
    public Optional<Teacher> findTeacherById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<Teacher> findTeacherByTeacherNumber(String teacherNumber) {
        return teacherRepository.selectByTeacherNumber(teacherNumber);
    }

    @Override
    public Optional<Teacher> findTeacherByUserId(Long userId) {
        return teacherRepository.selectByUserId(userId);
    }

    @Override
    public List<Teacher> findTeachersByName(String name) {
        return teacherRepository.selectByName(name);
    }

    @Override
    public List<Teacher> findTeachersByDepartment(String department) {
        return teacherRepository.selectByDepartment(department);
    }

    @Override
    public IPage<Teacher> findTeachersByPage(Integer current, Integer size) {
        Page<Teacher> page = new Page<>(current, size);
        return page(page, null);
    }

    @Override
    public IPage<Teacher> findTeachersByPageWithSearch(Integer current, Integer size, String teacherNumber, String name, String department) {
        // 这里暂时使用MyBatis Plus的查询方法，后续可以在TeacherRepository中添加对应的方法
        Page<Teacher> page = new Page<>(current, size);
        return lambdaQuery()
                .like(teacherNumber != null && !teacherNumber.isEmpty(), Teacher::getTeacherNumber, teacherNumber)
                .like(name != null && !name.isEmpty(), Teacher::getName, name)
                .like(department != null && !department.isEmpty(), Teacher::getDepartment, department)
                .page(page);
    }

    @Override
    public List<Teacher> findAllTeachers() {
        return teacherRepository.selectList(null);
    }

    @Override
    public List<Teacher> findTeachersByNameContaining(String name) {
        return teacherRepository.selectByNameContaining(name);
    }

    @Override
    public boolean existsByTeacherNumber(String teacherNumber) {
        return teacherRepository.existsByTeacherNumber(teacherNumber);
    }

    @Override
    public long countTeachers() {
        return teacherRepository.selectCount(null);
    }
}
