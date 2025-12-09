package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Major;
import com.example.studentselectionsystem.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 专业信息控制器
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/majors")
public class MajorController {

    @Autowired
    private MajorService majorService;

    /**
     * 创建专业
     */
    @PostMapping
    public ResponseEntity<Major> createMajor(@RequestBody Major major) {
        Major createdMajor = majorService.createMajor(major);
        return new ResponseEntity<>(createdMajor, HttpStatus.CREATED);
    }

    /**
     * 更新专业信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Major> updateMajor(@PathVariable Integer id, @RequestBody Major major) {
        Major updatedMajor = majorService.updateMajor(id, major);
        if (updatedMajor != null) {
            return new ResponseEntity<>(updatedMajor, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * 删除专业
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMajor(@PathVariable Integer id) {
        majorService.deleteMajor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 根据ID查找专业
     */
    @GetMapping("/{id}")
    public ResponseEntity<Major> findMajorById(@PathVariable Integer id) {
        Optional<Major> optionalMajor = majorService.findMajorById(id);
        return optionalMajor.map(major -> new ResponseEntity<>(major, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 根据专业名称查找专业
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Major> findMajorByName(@PathVariable String name) {
        Optional<Major> optionalMajor = majorService.findMajorByName(name);
        return optionalMajor.map(major -> new ResponseEntity<>(major, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 根据学院查找专业
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Major>> findMajorsByDepartment(@PathVariable String department) {
        List<Major> majors = majorService.findMajorsByDepartment(department);
        return new ResponseEntity<>(majors, HttpStatus.OK);
    }

    /**
     * 获取所有专业
     */
    @GetMapping("")
    public ResponseEntity<List<Major>> findAllMajors() {
        List<Major> majors = majorService.findAllMajors();
        return ResponseEntity.ok(majors);
    }

    /**
     * 分页获取专业
     * @param current 页码（从1开始）
     * @param size 每页大小
     */
    @GetMapping("/page")
    public ResponseEntity<IPage<Major>> findMajorsByPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        IPage<Major> majors = majorService.findMajorsByPage(current, size);
        return new ResponseEntity<>(majors, HttpStatus.OK);
    }

    /**
     * 检查专业名称是否已存在
     */
    @GetMapping("/check-name/{name}")
    public ResponseEntity<Boolean> checkNameExists(@PathVariable String name) {
        boolean exists = majorService.existsByName(name);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

}