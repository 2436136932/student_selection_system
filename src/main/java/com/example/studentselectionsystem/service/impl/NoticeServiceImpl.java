package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.example.studentselectionsystem.entity.Notice;
import com.example.studentselectionsystem.mapper.NoticeMapper;
import com.example.studentselectionsystem.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 通知Service实现类
 */
@Service
@Transactional
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public boolean addNotice(Notice notice) {
        // 设置创建时间和更新时间
        notice.setPublishTime(new Date());
        notice.setUpdateTime(new Date());
        return SqlHelper.retBool(noticeMapper.insert(notice));
    }

    @Override
    public boolean updateNotice(Notice notice) {
        // 设置更新时间
        notice.setUpdateTime(new Date());
        return SqlHelper.retBool(noticeMapper.updateById(notice));
    }

    @Override
    public boolean deleteNotice(Integer noticeId) {
        return SqlHelper.retBool(noticeMapper.deleteById(noticeId));
    }

    @Override
    public Optional<Notice> getNoticeById(Integer noticeId) {
        return Optional.ofNullable(noticeMapper.selectById(noticeId));
    }

    @Override
    public List<Notice> getAllPublishedNotices() {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // 只查询已发布的通知
        queryWrapper.orderByDesc("publish_time"); // 按发布时间倒序排序
        return noticeMapper.selectList(queryWrapper);
    }

    @Override
    public List<Notice> getRecentNotices() {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // 只查询已发布的通知
        queryWrapper.orderByDesc("publish_time"); // 按发布时间倒序排序
        
        // 创建分页对象，获取前10条记录
        Page<Notice> page = new Page<>(1, 10);
        return noticeMapper.selectPage(page, queryWrapper).getRecords();
    }

    @Override
    public IPage<Notice> getNoticesByPage(int page, int size, String title, Integer status) {
        // 创建分页对象
        Page<Notice> noticePage = new Page<>(page, size);
        
        // 创建查询条件
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            queryWrapper.like("title", title);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("publish_time");
        
        return noticeMapper.selectPage(noticePage, queryWrapper);
    }
}