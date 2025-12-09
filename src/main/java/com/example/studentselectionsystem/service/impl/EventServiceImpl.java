package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.example.studentselectionsystem.entity.Event;
import com.example.studentselectionsystem.mapper.EventMapper;
import com.example.studentselectionsystem.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 评选活动Service实现类
 */
@Service
@Transactional
public class EventServiceImpl implements EventService {

    @Autowired
    private EventMapper eventMapper;

    @Override
    public boolean addEvent(Event event) {
        // 设置创建时间
        event.setCreateTime(new Date());
        event.setUpdateTime(new Date());
        return SqlHelper.retBool(eventMapper.insert(event));
    }

    @Override
    public boolean updateEvent(Event event) {
        // 设置更新时间
        event.setUpdateTime(new Date());
        return SqlHelper.retBool(eventMapper.updateById(event));
    }

    @Override
    public boolean deleteEvent(Integer eventId) {
        return SqlHelper.retBool(eventMapper.deleteById(eventId));
    }

    @Override
    public Optional<Event> getEventById(Integer eventId) {
        return Optional.ofNullable(eventMapper.selectById(eventId));
    }

    @Override
    public List<Event> getAllEvents() {
        return eventMapper.selectList(null);
    }

    @Override
    public List<Event> getRecentEvents() {
        // 创建分页对象，获取前10条记录
        Page<Event> page = new Page<>(1, 10);
        
        // 创建查询条件，按截止日期倒序排序
        QueryWrapper<Event> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("deadline");
        
        return eventMapper.selectPage(page, queryWrapper).getRecords();
    }

    @Override
    public IPage<Event> getEventsByPage(int page, int size, String eventName, String status) {
        // 创建分页对象
        Page<Event> eventPage = new Page<>(page, size);
        return eventMapper.selectPage(eventPage, null);
    }
}