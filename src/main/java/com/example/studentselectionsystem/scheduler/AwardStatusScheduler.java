package com.example.studentselectionsystem.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.studentselectionsystem.entity.Award;
import com.example.studentselectionsystem.mapper.AwardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 奖项状态定时任务调度器
 * 用于定期检查奖项截止日期并自动更新状态
 */
@Component
public class AwardStatusScheduler {

    @Autowired
    private AwardMapper awardMapper;

    /**
     * 每天凌晨00:00执行一次
     * 检查所有奖项的截止日期，并更新状态
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateAwardStatusByDeadline() {
        // 获取当前时间
        Date now = new Date();
        
        // 查询所有已发布的奖项
        QueryWrapper<Award> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "已发布");
        List<Award> awards = awardMapper.selectList(queryWrapper);
        
        // 遍历奖项，检查截止日期
        for (Award award : awards) {
            Date endTime = award.getEndTime();
            if (endTime != null && now.after(endTime)) {
                // 超过截止日期，更新状态
                award.setStatus("已结束");
                award.setCurrentStatus("已关闭");
                awardMapper.updateById(award);
            }
        }
    }
}
