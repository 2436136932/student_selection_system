package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentselectionsystem.entity.Award;

import java.util.List;
import java.util.Optional;

/**
 * 奖项类型服务接口
 */
public interface AwardTypeService {
    
    /**
     * 获取奖项类型总数
     * @return 奖项类型总数
     */
    long countAwardTypes();
    
    /**
     * 根据ID获取奖项类型
     * @param id 奖项ID
     * @return 奖项类型信息
     */
    Optional<Award> getAwardTypeById(Integer id);
    
    /**
     * 获取所有奖项类型
     * @return 奖项类型列表
     */
    List<Award> getAllAwardTypes();
    
    /**
     * 分页获取奖项类型
     * @param page 页码
     * @param size 每页大小
     * @param awardName 奖项名称（可选）
     * @param awardType 奖项类型（可选）
     * @return 奖项类型分页列表
     */
    IPage<Award> getAwardTypesByPage(int page, int size, String awardName, String awardType);
    
    /**
     * 创建奖项类型
     * @param award 奖项类型信息
     * @return 创建的奖项类型
     */
    Award createAwardType(Award award);
    
    /**
     * 更新奖项类型
     * @param award 奖项类型信息
     * @return 更新后的奖项类型
     */
    Award updateAwardType(Award award);
    
    /**
     * 删除奖项类型
     * @param id 奖项ID
     */
    void deleteAwardType(Integer id);
    
    /**
     * 发布奖项
     * @param id 奖项ID
     * @return 发布后的奖项
     */
    Award publishAwardType(Integer id);
}
