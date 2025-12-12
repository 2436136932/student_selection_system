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
     * 检查所有奖项的状态和截止日期，并更新当前状态
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateAwardStatusByDeadline() {
        // 获取当前时间
        Date now = new Date();
        
        // 查询所有奖项
        List<Award> awards = awardMapper.selectList(null);
        
        // 遍历奖项，检查状态和截止日期
        for (Award award : awards) {
            Date endTime = award.getEndTime();
            String status = award.getStatus();
            String currentStatus = award.getCurrentStatus();
            boolean isEndTimeReached = endTime != null && now.after(endTime);
            String newCurrentStatus = currentStatus;
            boolean statusChanged = false;
            
            // 根据奖项状态和结束时间设置当前状态
            if ("未发布".equals(status)) {
                newCurrentStatus = "待开始";
            } else if ("已发布".equals(status)) {
                newCurrentStatus = isEndTimeReached ? "已关闭" : "进行中";
                // 如果已到结束时间，更新奖项状态为已结束
                if (isEndTimeReached) {
                    award.setStatus("已结束");
                    statusChanged = true;
                }
            } else if ("已结束".equals(status)) {
                newCurrentStatus = isEndTimeReached ? "已关闭" : "已完成";
            }
            
            // 无论当前状态是否变化，都强制更新当前状态
            award.setCurrentStatus(newCurrentStatus);
            awardMapper.updateById(award);
        }
    }
}
