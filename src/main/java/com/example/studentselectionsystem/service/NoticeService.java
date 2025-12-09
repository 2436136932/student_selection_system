package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Notice;

import java.util.List;
import java.util.Optional;

/**
 * 通知Service接口
 */
public interface NoticeService {
    /**
     * 新增通知
     * @param notice 通知信息
     * @return 是否成功
     */
    boolean addNotice(Notice notice);
    
    /**
     * 更新通知信息
     * @param notice 通知信息
     * @return 是否成功
     */
    boolean updateNotice(Notice notice);
    
    /**
     * 删除通知
     * @param noticeId 通知ID
     * @return 是否成功
     */
    boolean deleteNotice(Integer noticeId);
    
    /**
     * 根据通知ID查询通知
     * @param noticeId 通知ID
     * @return 通知信息
     */
    Optional<Notice> getNoticeById(Integer noticeId);
    
    /**
     * 查询所有已发布的通知
     * @return 通知列表
     */
    List<Notice> getAllPublishedNotices();
    
    /**
     * 获取最近的通知（前10个）
     * @return 通知列表
     */
    List<Notice> getRecentNotices();
    
    /**
     * 分页查询通知
     * @param page 页码
     * @param size 每页数量
     * @param title 通知标题（可选）
     * @param status 通知状态（可选）
     * @return 分页通知列表
     */
    IPage<Notice> getNoticesByPage(int page, int size, String title, Integer status);
}
