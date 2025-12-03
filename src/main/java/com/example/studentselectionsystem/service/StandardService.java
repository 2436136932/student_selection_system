package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Standard;

import java.util.List;
import java.util.Optional;

/**
 * 评奖标准服务接口
 */
public interface StandardService {

    /**
     * 创建评奖标准
     * @param standard 评奖标准信息
     * @return 创建的评奖标准
     */
    Standard createStandard(Standard standard);

    /**
     * 更新评奖标准信息
     * @param id 标准ID
     * @param standard 标准信息
     * @return 更新后的标准
     */
    Standard updateStandard(Integer id, Standard standard);

    /**
     * 删除评奖标准
     * @param id 标准ID
     */
    void deleteStandard(Integer id);

    /**
     * 根据ID查找标准
     * @param id 标准ID
     * @return 标准信息
     */
    Optional<Standard> findStandardById(Integer id);

    /**
     * 根据标准名称查找标准
     * @param name 标准名称
     * @return 标准信息
     */
    Optional<Standard> findStandardByName(String name);

    /**
     * 根据标准代码查找标准
     * @param code 标准代码
     * @return 标准信息
     */
    Optional<Standard> findStandardByCode(String code);

    /**
     * 获取所有标准
     * @return 标准列表
     */
    List<Standard> findAllStandards();

    /**
     * 分页获取标准
     * @param page 分页参数
     * @return 标准分页列表
     */
    IPage<Standard> findStandardsByPage(IPage<Standard> page);

    /**
     * 分页获取标准
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @return 标准分页列表
     */
    IPage<Standard> findStandardsByPage(Integer current, Integer size);

    /**
     * 判断标准名称是否存在
     * @param name 标准名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 判断标准代码是否存在
     * @param code 标准代码
     * @return 是否存在
     */
    boolean existsByCode(String code);
}