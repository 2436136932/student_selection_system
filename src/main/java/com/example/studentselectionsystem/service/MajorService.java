package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Major;

import java.util.List;
import java.util.Optional;

/**
 * 专业信息服务接口
 */
public interface MajorService {

    /**
     * 创建专业
     * @param major 专业信息
     * @return 创建的专业
     */
    Major createMajor(Major major);

    /**
     * 更新专业信息
     * @param id 专业ID
     * @param major 专业信息
     * @return 更新后的专业
     */
    Major updateMajor(Integer id, Major major);

    /**
     * 删除专业
     * @param id 专业ID
     */
    void deleteMajor(Integer id);

    /**
     * 根据ID查找专业
     * @param id 专业ID
     * @return 专业信息
     */
    Optional<Major> findMajorById(Integer id);

    /**
     * 根据专业名称查找专业
     * @param name 专业名称
     * @return 专业信息
     */
    Optional<Major> findMajorByName(String name);

    /**
     * 根据学院查找专业
     * @param department 学院
     * @return 专业列表
     */
    List<Major> findMajorsByDepartment(String department);

    /**
     * 获取所有专业
     * @return 专业列表
     */
    List<Major> findAllMajors();

    /**
     * 分页获取专业
     * @param page 分页参数
     * @return 专业分页列表
     */
    IPage<Major> findMajorsByPage(IPage<Major> page);

    /**
     * 分页获取专业
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @return 专业分页列表
     */
    IPage<Major> findMajorsByPage(Integer current, Integer size);
    
    /**
     * 按院系筛选并分页获取专业
     * @param page 分页参数
     * @param department 院系名称
     * @return 专业分页列表
     */
    IPage<Major> findMajorsByDepartmentAndPage(IPage<Major> page, String department);

    /**
     * 判断专业名称是否存在
     * @param name 专业名称
     * @return 是否存在
     */
    boolean existsByName(String name);

}