package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Event;
import com.example.studentselectionsystem.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 评选活动控制器
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    /**
     * 创建评选活动
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        try {
            boolean success = eventService.addEvent(event);
            if (success) {
                return new ResponseEntity<>(event, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 更新评选活动
     */
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Event> updateEvent(@RequestBody Event event) {
        try {
            boolean success = eventService.updateEvent(event);
            if (success) {
                return new ResponseEntity<>(event, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除评选活动
     */
    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> deleteEvent(@PathVariable Integer eventId) {
        try {
            boolean success = eventService.deleteEvent(eventId);
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
     * 根据ID获取评选活动
     */
    @GetMapping("/{eventId}")
    public ResponseEntity<Event> getEventById(@PathVariable Integer eventId) {
        try {
            Optional<Event> event = eventService.getEventById(eventId);
            if (event.isPresent()) {
                return new ResponseEntity<>(event.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取所有评选活动
     */
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        try {
            List<Event> events = eventService.getAllEvents();
            return new ResponseEntity<>(events, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取最近的评选活动
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Event>> getRecentEvents() {
        try {
            List<Event> events = eventService.getRecentEvents();
            return new ResponseEntity<>(events, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 分页查询评选活动
     */
    @GetMapping("/page")
    public ResponseEntity<IPage<Event>> getEventsByPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String eventName,
            @RequestParam(required = false) String status) {
        try {
            IPage<Event> eventPage = eventService.getEventsByPage(page, size, eventName, status);
            return new ResponseEntity<>(eventPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}