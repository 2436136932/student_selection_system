package com.example.studentselectionsystem.controller;

import com.example.studentselectionsystem.entity.SelectionCriteria;
import com.example.studentselectionsystem.service.SelectionCriteriaService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 评选标准控制器
 */
@RestController
@RequestMapping("/api/selection-criteria")
public class SelectionCriteriaController {

    @Autowired
    private SelectionCriteriaService selectionCriteriaService;

    /**
     * 创建评选标准
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<SelectionCriteria> createSelectionCriteria(@RequestBody SelectionCriteria selectionCriteria) {
        SelectionCriteria createdCriteria = selectionCriteriaService.createSelectionCriteria(selectionCriteria);
        return new ResponseEntity<>(createdCriteria, HttpStatus.CREATED);
    }

    /**
     * 更新评选标准
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<SelectionCriteria> updateSelectionCriteria(@PathVariable Integer id, @RequestBody SelectionCriteria selectionCriteria) {
        SelectionCriteria updatedCriteria = selectionCriteriaService.updateSelectionCriteria(id, selectionCriteria);
        if (updatedCriteria != null) {
            return new ResponseEntity<>(updatedCriteria, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * 删除评选标准
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Void> deleteSelectionCriteria(@PathVariable Integer id) {
        selectionCriteriaService.deleteSelectionCriteria(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 根据ID查找评选标准
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<SelectionCriteria> findSelectionCriteriaById(@PathVariable Integer id) {
        Optional<SelectionCriteria> optionalCriteria = selectionCriteriaService.findSelectionCriteriaById(id);
        return optionalCriteria.map(criteria -> new ResponseEntity<>(criteria, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 根据奖项ID查找评选标准
     */
    @GetMapping("/award/{awardId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<List<SelectionCriteria>> findSelectionCriteriaByAwardId(@PathVariable Integer awardId) {
        List<SelectionCriteria> criteria = selectionCriteriaService.findSelectionCriteriaByAwardId(awardId);
        return new ResponseEntity<>(criteria, HttpStatus.OK);
    }

    /**
     * 获取所有评选标准
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<SelectionCriteria>> findAllSelectionCriteria() {
        List<SelectionCriteria> criteria = selectionCriteriaService.findAllSelectionCriteria();
        return new ResponseEntity<>(criteria, HttpStatus.OK);
    }

    /**
     * 分页获取评选标准
     */
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Page<SelectionCriteria>> findSelectionCriteriaByPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<SelectionCriteria> page = new Page<>(current, size);
        Page<SelectionCriteria> criteriaPage = selectionCriteriaService.findSelectionCriteriaByPage(page);
        return new ResponseEntity<>(criteriaPage, HttpStatus.OK);
    }

    /**
     * 计算奖项的总权重
     */
    @GetMapping("/calculate-total-weight/{awardId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Double> calculateTotalWeightByAwardId(@PathVariable Integer awardId) {
        Double totalWeight = selectionCriteriaService.calculateTotalWeightByAwardId(awardId);
        return new ResponseEntity<>(totalWeight, HttpStatus.OK);
    }
}
