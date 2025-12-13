package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Major;
import com.example.studentselectionsystem.repository.MajorRepository;
import com.example.studentselectionsystem.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 专业信息服务实现类
 */
@Service
@Transactional
public class MajorServiceImpl implements MajorService {

    @Autowired
    private MajorRepository majorRepository;

    @Override
    public Major createMajor(Major major) {
        // 设置创建时间
        major.setCreateTime(new Date());
        System.out.println("MajorServiceImpl - 创建专业: " + major);
        System.out.println("MajorServiceImpl - 专业名称: " + major.getName());
        System.out.println("MajorServiceImpl - 专业所属学院: " + major.getDepartment());
        System.out.println("MajorServiceImpl - 专业ID: " + major.getId());
        
        try {
            int result = majorRepository.insert(major);
            System.out.println("MajorServiceImpl - 插入结果: " + result);
            System.out.println("MajorServiceImpl - 插入后专业ID: " + major.getId());
            return major;
        } catch (Exception e) {
            System.out.println("MajorServiceImpl - 插入失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Major updateMajor(Integer id, Major major) {
        Optional<Major> optionalMajor = Optional.ofNullable(majorRepository.selectById(id));
        if (optionalMajor.isPresent()) {
            Major existingMajor = optionalMajor.get();
            // 更新专业信息
            existingMajor.setName(major.getName());
            existingMajor.setDepartment(major.getDepartment());
            majorRepository.updateById(existingMajor);
            return existingMajor;
        }
        return null;
    }

    @Override
    public void deleteMajor(Integer id) {
        majorRepository.deleteById(id);
    }

    @Override
    public Optional<Major> findMajorById(Integer id) {
        return Optional.ofNullable(majorRepository.selectById(id));
    }

    @Override
    public Optional<Major> findMajorByName(String name) {
        return majorRepository.selectByName(name);
    }

    @Override
    public List<Major> findMajorsByDepartment(String department) {
        return majorRepository.selectByDepartment(department);
    }

    @Override
    public List<Major> findAllMajors() {
        return majorRepository.selectList(null);
    }

    @Override
    public IPage<Major> findMajorsByPage(IPage<Major> page) {
        return majorRepository.selectPage(page, null);
    }
    
    @Override
    public IPage<Major> findMajorsByDepartmentAndPage(IPage<Major> page, String department) {
        // 创建查询条件
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department", department);
        return majorRepository.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<Major> findMajorsByPage(Integer current, Integer size) {
        // 创建MyBatis Plus分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Major> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        return majorRepository.selectPage(page, null);
    }

    @Override
    public boolean existsByName(String name) {
        return majorRepository.existsByName(name);
    }

}