package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Notice;
import com.example.studentselectionsystem.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 创建通知
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Notice> createNotice(@RequestBody Notice notice) {
        try {
            boolean success = noticeService.addNotice(notice);
            if (success) {
                return new ResponseEntity<>(notice, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 更新通知
     */
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Notice> updateNotice(@RequestBody Notice notice) {
        try {
            boolean success = noticeService.updateNotice(notice);
            if (success) {
                return new ResponseEntity<>(notice, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{noticeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> deleteNotice(@PathVariable Integer noticeId) {
        try {
            boolean success = noticeService.deleteNotice(noticeId);
            if (success) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据ID获取通知
     */
    @GetMapping("/{noticeId}")
    public ResponseEntity<Notice> getNoticeById(@PathVariable Integer noticeId) {
        try {
            Optional<Notice> notice = noticeService.getNoticeById(noticeId);
            if (notice.isPresent()) {
                return new ResponseEntity<>(notice.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取所有已发布的通知
     */
    @GetMapping
    public ResponseEntity<List<Notice>> getAllPublishedNotices() {
        try {
            List<Notice> notices = noticeService.getAllPublishedNotices();
            return new ResponseEntity<>(notices, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取最近的通知
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Notice>> getRecentNotices() {
        try {
            List<Notice> notices = noticeService.getRecentNotices();
            return new ResponseEntity<>(notices, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 分页查询通知
     */
    @GetMapping("/page")
    public ResponseEntity<IPage<Notice>> getNoticesByPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status) {
        try {
            IPage<Notice> noticePage = noticeService.getNoticesByPage(page, size, title, status);
            return new ResponseEntity<>(noticePage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 批量删除通知
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> batchDeleteNotices(@RequestBody List<Integer> noticeIds) {
        try {
            boolean success = noticeService.batchDeleteNotices(noticeIds);
            if (success) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}