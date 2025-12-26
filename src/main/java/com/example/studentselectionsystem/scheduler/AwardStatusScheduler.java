package com.example.studentselectionsystem.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.studentselectionsystem.entity.Award;
import com.example.studentselectionsystem.entity.Student;
import com.example.studentselectionsystem.entity.StudentAwardRecord;
import com.example.studentselectionsystem.mapper.AwardMapper;
import com.example.studentselectionsystem.mapper.StudentAwardRecordMapper;
import com.example.studentselectionsystem.mapper.StudentMapper;
import com.example.studentselectionsystem.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger logger = LoggerFactory.getLogger(AwardStatusScheduler.class);

    @Autowired
    private AwardMapper awardMapper;
    
    @Autowired
    private StudentAwardRecordMapper studentAwardRecordMapper;
    
    @Autowired
    private StudentMapper studentMapper;
    
    @Autowired
    private EmailService emailService;

    /**
     * 每10秒执行一次，用于快速测试
     * 检查所有奖项的状态和截止日期，并更新当前状态
     */
    @Scheduled(cron = "*/10 * * * * ?")
    public void updateAwardStatusByDeadline() {
        logger.info("开始执行奖项状态更新定时任务");
        // 获取当前时间
        Date now = new Date();
        
        // 查询所有奖项
        List<Award> awards = awardMapper.selectList(null);
        logger.info("共查询到 {} 个奖项", awards.size());
        
        // 遍历奖项，检查状态和截止日期
        for (Award award : awards) {
            logger.info("处理奖项: {}，当前状态: {}，当前阶段: {}，结束时间: {}", 
                award.getAwardName(), award.getStatus(), award.getCurrentStatus(), award.getEndTime());
            
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
                    logger.info("奖项 {} 已到结束时间，更新状态为已结束", award.getAwardName());
                    award.setStatus("已结束");
                    statusChanged = true;
                    
                    // 检查邮件是否已发送，未发送则发送
                    if (award.getEmailSent() == null || award.getEmailSent() == 0) {
                        sendAwardNotificationToStudents(award);
                        award.setEmailSent(1);
                        logger.info("奖项 {} 邮件发送完成，标记为已发送", award.getAwardName());
                    } else {
                        logger.info("奖项 {} 邮件已发送，跳过", award.getAwardName());
                    }
                }
            } else if ("已结束".equals(status)) {
                newCurrentStatus = isEndTimeReached ? "已关闭" : "已完成";
                
                // 检查邮件是否已发送，未发送则发送
                if (award.getEmailSent() == null || award.getEmailSent() == 0) {
                    logger.info("奖项 {} 已结束但邮件未发送，开始发送", award.getAwardName());
                    sendAwardNotificationToStudents(award);
                    award.setEmailSent(1);
                    logger.info("奖项 {} 邮件发送完成，标记为已发送", award.getAwardName());
                } else {
                    logger.info("奖项 {} 邮件已发送，跳过", award.getAwardName());
                }
            }
            
            // 无论当前状态是否变化，都强制更新当前状态
            award.setCurrentStatus(newCurrentStatus);
            awardMapper.updateById(award);
            logger.info("奖项 {} 更新完成，新状态: {}，新当前状态: {}", 
                award.getAwardName(), award.getStatus(), newCurrentStatus);
        }
        logger.info("奖项状态更新定时任务执行完成");
    }
    
    /**
     * 发送邮件通知获奖学生
     * @param award 已结束的奖项
     */
    private void sendAwardNotificationToStudents(Award award) {
        logger.info("开始发送获奖通知邮件，奖项: {}", award.getAwardName());
        
        // 查询该奖项的所有获奖学生
        QueryWrapper<StudentAwardRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("award_id", award.getId());
        List<StudentAwardRecord> records = studentAwardRecordMapper.selectList(queryWrapper);
        
        logger.info("奖项 {} 共有 {} 名获奖学生", award.getAwardName(), records.size());
        
        // 遍历获奖记录，发送邮件通知
        for (StudentAwardRecord record : records) {
            logger.info("处理获奖学生: {}，学号: {}", record.getStudentName(), record.getStudentNumber());
            
            // 根据student_id查询学生信息获取邮箱
            Student student = studentMapper.selectById(record.getStudentId());
            if (student != null) {
                logger.info("找到学生: {}，邮箱: {}", student.getName(), student.getEmail());
                
                if (student.getEmail() != null && !student.getEmail().isEmpty()) {
                    // 发送邮件
                    logger.info("准备发送邮件给学生: {}，邮箱: {}，奖项: {}", 
                        student.getName(), student.getEmail(), award.getAwardName());
                    
                    emailService.sendAwardNotificationEmail(
                        record.getStudentName(),
                        award.getAwardName(),
                        award.getAwardLevel(),
                        student.getEmail()
                    );
                    
                    logger.info("邮件发送成功: 学生 {}，奖项 {}", student.getName(), award.getAwardName());
                } else {
                    logger.warn("学生 {} 没有设置邮箱，无法发送邮件", student.getName());
                }
            } else {
                logger.warn("未找到学生信息，学生ID: {}", record.getStudentId());
            }
        }
        
        logger.info("获奖通知邮件发送完成，奖项: {}", award.getAwardName());
    }
}
