package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Standard;
import com.example.studentselectionsystem.service.StandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 评奖标准控制器
 */
@RestController
@RequestMapping("/api/standards")
public class StandardController {

    @Autowired
    private StandardService standardService;

    /**
     * 创建评奖标准
     * @param standard 评奖标准信息
     * @return 创建的评奖标准
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PostMapping
    public ResponseEntity<Standard> createStandard(@RequestBody Standard standard) {
        try {
            Standard createdStandard = standardService.createStandard(standard);
            return new ResponseEntity<>(createdStandard, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 更新评奖标准信息
     * @param id 标准ID
     * @param standard 标准信息
     * @return 更新后的标准
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PutMapping("/{id}")
    public ResponseEntity<Standard> updateStandard(@PathVariable("id") Integer id, @RequestBody Standard standard) {
        try {
            Standard updatedStandard = standardService.updateStandard(id, standard);
            if (updatedStandard != null) {
                return new ResponseEntity<>(updatedStandard, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除评奖标准
     * @param id 标准ID
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteStandard(@PathVariable("id") Integer id) {
        try {
            standardService.deleteStandard(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据ID查找评奖标准
     * @param id 标准ID
     * @return 标准信息
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/{id}")
    public ResponseEntity<Standard> findStandardById(@PathVariable("id") Integer id) {
        Optional<Standard> standard = standardService.findStandardById(id);
        if (standard.isPresent()) {
            return new ResponseEntity<>(standard.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 根据标准名称查找标准
     * @param name 标准名称
     * @return 标准信息
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/name/{name}")
    public ResponseEntity<Standard> findStandardByName(@PathVariable("name") String name) {
        Optional<Standard> standard = standardService.findStandardByName(name);
        if (standard.isPresent()) {
            return new ResponseEntity<>(standard.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 根据标准代码查找标准
     * @param code 标准代码
     * @return 标准信息
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<Standard> findStandardByCode(@PathVariable("code") String code) {
        Optional<Standard> standard = standardService.findStandardByCode(code);
        if (standard.isPresent()) {
            return new ResponseEntity<>(standard.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 获取所有评奖标准
     * @return 标准列表
     */
    @GetMapping
    public ResponseEntity<List<Standard>> findAllStandards() {
        try {
            List<Standard> standards = standardService.findAllStandards();
            if (standards.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(standards, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 分页获取评奖标准，支持搜索
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @param code 标准代码（可选）
     * @param name 标准名称（可选）
     * @param teacher 负责人（可选）
     * @return 标准分页列表
     */
    @GetMapping("/page")
    public ResponseEntity<IPage<Standard>> findStandardsByPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String teacher) {
        try {
            IPage<Standard> standards = standardService.findStandardsByPage(current, size, code, name, teacher);
            return new ResponseEntity<>(standards, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 判断标准名称是否存在
     * @param name 标准名称
     * @return 是否存在
     */
    @GetMapping("/exists/name/{name}")
    public ResponseEntity<Boolean> existsByName(@PathVariable("name") String name) {
        try {
            boolean exists = standardService.existsByName(name);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 判断标准代码是否存在
     * @param code 标准代码
     * @return 是否存在
     */
    @GetMapping("/exists/code/{code}")
    public ResponseEntity<Boolean> existsByCode(@PathVariable("code") String code) {
        try {
            boolean exists = standardService.existsByCode(code);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}