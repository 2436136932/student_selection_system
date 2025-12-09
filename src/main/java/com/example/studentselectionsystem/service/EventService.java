package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Event;

import java.util.List;
import java.util.Optional;

/**
 * 评选活动Service接口
 */
public interface EventService {
    /**
     * 新增评选活动
     * @param event 评选活动信息
     * @return 是否成功
     */
    boolean addEvent(Event event);
    
    /**
     * 更新评选活动信息
     * @param event 评选活动信息
     * @return 是否成功
     */
    boolean updateEvent(Event event);
    
    /**
     * 删除评选活动
     * @param eventId 评选活动ID
     * @return 是否成功
     */
    boolean deleteEvent(Integer eventId);
    
    /**
     * 根据评选活动ID查询评选活动
     * @param eventId 评选活动ID
     * @return 评选活动信息
     */
    Optional<Event> getEventById(Integer eventId);
    
    /**
     * 查询所有评选活动
     * @return 评选活动列表
     */
    List<Event> getAllEvents();
    
    /**
     * 获取最近的评选活动（前10个）
     * @return 评选活动列表
     */
    List<Event> getRecentEvents();
    
    /**
     * 分页查询评选活动
     * @param page 页码
     * @param size 每页数量
     * @param eventName 活动名称（可选）
     * @param status 活动状态（可选）
     * @return 分页评选活动列表
     */
    IPage<Event> getEventsByPage(int page, int size, String eventName, String status);
}