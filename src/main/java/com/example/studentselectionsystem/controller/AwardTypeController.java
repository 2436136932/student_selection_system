package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Award;
import com.example.studentselectionsystem.service.AwardTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

/**
 * 奖项类型Controller
 */
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/award-types")
public class AwardTypeController {
    
    @Autowired
    private AwardTypeService awardTypeService;

    /**
     * 新增奖项类型
     * @param award 奖项类型信息
     * @return 响应结果
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Award> addAwardType(@RequestBody Award award) {
        Award createdAward = awardTypeService.createAwardType(award);
        return ResponseEntity.ok(createdAward);
    }

    /**
     * 更新奖项类型
     * @param id 奖项类型ID
     * @param award 奖项类型信息
     * @return 响应结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Award> updateAwardType(@PathVariable Long id, @RequestBody Award award) {
        // 确保路径参数和请求体中的ID一致
        award.setId(id);
        Award updatedAward = awardTypeService.updateAwardType(award);
        if (updatedAward != null) {
            return ResponseEntity.ok(updatedAward);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除奖项类型
     * @param id 奖项类型ID
     * @return 响应结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAwardType(@PathVariable Long id) {
        awardTypeService.deleteAwardType(id);
        return ResponseEntity.ok("删除奖项类型成功");
    }

    /**
     * 根据奖项类型ID查询奖项类型
     * @param id 奖项类型ID
     * @return 奖项类型信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Award> getAwardTypeById(@PathVariable Long id) {
        Optional<Award> award = awardTypeService.getAwardTypeById(id);
        return award.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 查询所有奖项类型
     * @return 奖项类型列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<?> getAwardTypesByPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String awardName,
            @RequestParam(required = false) String awardType) {
        
        IPage<Award> awards = awardTypeService.getAwardTypesByPage(page, size, awardName, awardType);
        
        // 转换为前端期望的格式（Spring Data JPA Page格式）
        return ResponseEntity.ok(new Object() {
            public final List<Award> content = awards.getRecords();
            public final long totalElements = awards.getTotal();
        });
    }

    /**
     * 获取所有奖项类型（不分页）
     * @return 奖项类型列表
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<List<Award>> getAllAwardTypes() {
        List<Award> awards = awardTypeService.getAllAwardTypes();
        return ResponseEntity.ok(awards);
    }
    
    /**
     * 发布奖项
     * @param id 奖项ID
     * @return 响应结果
     */
    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Award> publishAwardType(@PathVariable Long id) {
        Award publishedAward = awardTypeService.publishAwardType(id);
        if (publishedAward != null) {
            return ResponseEntity.ok(publishedAward);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
