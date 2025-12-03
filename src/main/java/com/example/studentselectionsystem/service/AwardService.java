package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.model.Award;
import java.util.List;
import java.util.Optional;

/**
 * 评奖评优Service接口
 */
public interface AwardService {
    /**
     * 新增奖项
     * @param award 奖项信息
     * @return 是否成功
     */
    boolean addAward(Award award);
    
    /**
     * 更新奖项信息
     * @param award 奖项信息
     * @return 是否成功
     */
    boolean updateAward(Award award);
    
    /**
     * 删除奖项
     * @param awardId 奖项ID
     * @return 是否成功
     */
    boolean deleteAward(String awardId);
    
    /**
     * 根据奖项ID查询奖项
     * @param awardId 奖项ID
     * @return 奖项信息
     */
    Optional<Award> getAwardById(String awardId);
    
    /**
     * 根据学生学号查询所有奖项
     * @param studentId 学生学号
     * @return 奖项列表
     */
    List<Award> getAwardsByStudentId(String studentId);
    
    /**
     * 查询所有奖项
     * @return 奖项列表
     */
    List<Award> getAllAwards();
    
    /**
     * 分页查询奖项
     * @param page 页码
     * @param size 每页数量
     * @param studentId 学生学号（可选）
     * @param awardName 奖项名称（可选）
     * @param year 年份（可选）
     * @return 分页奖项列表
     */
    IPage<Award> getAwardsByPage(int page, int size, String studentId, String awardName, String year);
    
    /**
     * 获取学生获奖次数
     * @param studentId 学生学号
     * @return 获奖次数
     */
    int getAwardCountByStudentId(String studentId);
}