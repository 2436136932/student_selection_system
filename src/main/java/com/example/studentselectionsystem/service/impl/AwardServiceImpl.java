package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentselectionsystem.dao.AwardDAO;
import com.example.studentselectionsystem.mapper.AwardMapper;
import com.example.studentselectionsystem.model.Award;
import com.example.studentselectionsystem.service.AwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * 评奖评优Service实现类
 */
@Service
public class AwardServiceImpl implements AwardService {
    
    @Autowired
    private AwardDAO awardDAO;
    
    @Autowired
    private AwardMapper awardMapper;

    @Override
    public boolean addAward(Award award) {
        return awardDAO.addAward(award);
    }

    @Override
    public boolean updateAward(Award award) {
        return awardDAO.updateAward(award);
    }

    @Override
    public boolean deleteAward(String awardId) {
        return awardDAO.deleteAward(awardId);
    }

    @Override
    public Optional<Award> getAwardById(String awardId) {
        return awardDAO.getAwardById(awardId);
    }

    @Override
    public List<Award> getAwardsByStudentId(String studentId) {
        return awardDAO.getAwardsByStudentId(studentId);
    }

    @Override
    public List<Award> getAllAwards() {
        return awardDAO.getAllAwards();
    }

    @Override
    public IPage<Award> getAwardsByPage(int page, int size, String studentId, String awardName, String year) {
        // 获取当前页的奖项列表
        List<Award> awards = awardDAO.getAwardsByPage(page, size, studentId, awardName, year);
        // 获取总记录数
        long total = awardDAO.getTotalAwards(studentId, awardName, year);
        // 创建MyBatis Plus的Page对象
        IPage<Award> resultPage = new Page<>(page, size);
        resultPage.setRecords(awards);
        resultPage.setTotal(total);
        // 返回IPage对象
        return resultPage;
    }

    @Override
    public int getAwardCountByStudentId(String studentId) {
        return awardDAO.getAwardsByStudentId(studentId).size();
    }

    @Override
    public long countAwards() {
        try {
            // 使用MyBatis Plus的selectCount方法直接查询数据库中的奖项总数
            return awardMapper.selectCount(null);
        } catch (Exception e) {
            // 添加错误日志
            System.err.println("Error in countAwards: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}