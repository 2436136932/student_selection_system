package com.example.studentselectionsystem.service;

import com.example.studentselectionsystem.entity.SelectionCriteria;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Optional;

/**
 * 评选标准服务接口
 */
public interface SelectionCriteriaService {

    /**
     * 创建评选标准
     * @param selectionCriteria 评选标准信息
     * @return 创建的评选标准
     */
    SelectionCriteria createSelectionCriteria(SelectionCriteria selectionCriteria);

    /**
     * 更新评选标准
     * @param id 评选标准ID
     * @param selectionCriteria 评选标准信息
     * @return 更新后的评选标准
     */
    SelectionCriteria updateSelectionCriteria(Integer id, SelectionCriteria selectionCriteria);

    /**
     * 删除评选标准
     * @param id 评选标准ID
     */
    void deleteSelectionCriteria(Integer id);

    /**
     * 根据ID查找评选标准
     * @param id 评选标准ID
     * @return 评选标准信息
     */
    Optional<SelectionCriteria> findSelectionCriteriaById(Integer id);

    /**
     * 根据奖项ID查找评选标准
     * @param awardId 奖项ID
     * @return 评选标准列表
     */
    List<SelectionCriteria> findSelectionCriteriaByAwardId(Integer awardId);

    /**
     * 获取所有评选标准
     * @return 评选标准列表
     */
    List<SelectionCriteria> findAllSelectionCriteria();

    /**
     * 分页获取评选标准
     * @param page 分页参数
     * @return 评选标准分页列表
     */
    Page<SelectionCriteria> findSelectionCriteriaByPage(Page<SelectionCriteria> page);

    /**
     * 计算奖项的总权重
     * @param awardId 奖项ID
     * @return 总权重
     */
    Double calculateTotalWeightByAwardId(Integer awardId);
}
