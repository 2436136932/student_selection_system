package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Standard;
import com.example.studentselectionsystem.repository.StandardRepository;
import com.example.studentselectionsystem.service.StandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 评奖标准服务实现类
 */
@Service
public class StandardServiceImpl implements StandardService {

    @Autowired
    private StandardRepository standardRepository;

    /**
     * 创建评奖标准
     * @param standard 评奖标准信息
     * @return 创建的评奖标准
     */
    @Override
    public Standard createStandard(Standard standard) {
        standardRepository.insert(standard);
        return standard;
    }

    /**
     * 更新评奖标准信息
     * @param id 标准ID
     * @param standard 标准信息
     * @return 更新后的标准
     */
    @Override
    public Standard updateStandard(Integer id, Standard standard) {
        Optional<Standard> existingStandard = Optional.ofNullable(standardRepository.selectById(id));
        if (existingStandard.isPresent()) {
            standard.setId(id); // 确保ID正确
            standardRepository.updateById(standard);
            return standardRepository.selectById(id);
        }
        return null;
    }

    /**
     * 删除评奖标准
     * @param id 标准ID
     */
    @Override
    public void deleteStandard(Integer id) {
        standardRepository.deleteById(id);
    }

    /**
     * 根据ID查找标准
     * @param id 标准ID
     * @return 标准信息
     */
    @Override
    public Optional<Standard> findStandardById(Integer id) {
        return Optional.ofNullable(standardRepository.selectById(id));
    }

    /**
     * 根据标准名称查找标准
     * @param name 标准名称
     * @return 标准信息
     */
    @Override
    public Optional<Standard> findStandardByName(String name) {
        return standardRepository.selectByName(name);
    }

    /**
     * 根据标准代码查找标准
     * @param code 标准代码
     * @return 标准信息
     */
    @Override
    public Optional<Standard> findStandardByCode(String code) {
        return standardRepository.selectByCode(code);
    }

    /**
     * 获取所有标准
     * @return 标准列表
     */
    @Override
    public List<Standard> findAllStandards() {
        return standardRepository.selectList(null);
    }

    /**
     * 分页获取标准
     * @param page 分页参数
     * @return 标准分页列表
     */
    @Override
    public IPage<Standard> findStandardsByPage(IPage<Standard> page) {
        return standardRepository.selectPage(page, null);
    }

    /**
     * 分页获取标准
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @return 标准分页列表
     */
    @Override
    public IPage<Standard> findStandardsByPage(Integer current, Integer size) {
        // 创建Standard的分页对象
        IPage<Standard> standardPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        return standardRepository.selectPage(standardPage, null);
    }

    /**
     * 判断标准名称是否存在
     * @param name 标准名称
     * @return 是否存在
     */
    @Override
    public boolean existsByName(String name) {
        return standardRepository.existsByName(name);
    }

    /**
     * 判断标准代码是否存在
     * @param code 标准代码
     * @return 是否存在
     */
    @Override
    public boolean existsByCode(String code) {
        return standardRepository.existsByCode(code);
    }

    /**
     * 分页获取标准，支持搜索
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @param code 标准代码（可选）
     * @param name 标准名称（可选）
     * @param teacher 负责人（可选）
     * @return 标准分页列表
     */
    @Override
    public IPage<Standard> findStandardsByPage(Integer current, Integer size, String code, String name, String teacher) {
        // 创建Standard的分页对象
        IPage<Standard> standardPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        
        // 构建查询条件
        QueryWrapper<Standard> queryWrapper = new QueryWrapper<>();
        
        // 添加搜索条件
        if (code != null && !code.isEmpty()) {
            queryWrapper.like("code", code);
        }
        if (name != null && !name.isEmpty()) {
            queryWrapper.like("name", name);
        }
        if (teacher != null && !teacher.isEmpty()) {
            queryWrapper.like("teacher", teacher);
        }
        
        // 执行分页查询
        return standardRepository.selectPage(standardPage, queryWrapper);
    }
}