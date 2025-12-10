package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.model.Award;
import com.example.studentselectionsystem.service.AwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

/**
 * 评奖评优Controller
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/awards")
public class AwardController {
    
    @Autowired
    private AwardService awardService;

    /**
     * 新增奖项
     * @param award 奖项信息
     * @return 响应结果
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addAward(@RequestBody Award award) {
        boolean success = awardService.addAward(award);
        if (success) {
            return ResponseEntity.ok("新增奖项成功");
        } else {
            return ResponseEntity.badRequest().body("新增奖项失败，奖项ID已存在");
        }
    }

    /**
     * 更新奖项信息
     * @param id 奖项ID
     * @param award 奖项信息
     * @return 响应结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateAward(@PathVariable Integer id, @RequestBody Award award) {
        // 确保路径参数和请求体中的ID一致
        award.setAwardId(String.valueOf(id));
        boolean success = awardService.updateAward(award);
        if (success) {
            return ResponseEntity.ok("更新奖项成功");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除奖项
     * @param id 奖项ID
     * @return 响应结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAward(@PathVariable Integer id) {
        boolean success = awardService.deleteAward(String.valueOf(id));
        if (success) {
            return ResponseEntity.ok("删除奖项成功");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 根据奖项ID查询奖项
     * @param awardId 奖项ID
     * @return 奖项信息
     */
    @GetMapping("/{awardId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Award> getAwardById(@PathVariable String awardId) {
        Optional<Award> award = awardService.getAwardById(awardId);
        return award.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 根据学生学号查询所有奖项
     * @param studentId 学生学号
     * @return 奖项列表
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or authentication.principal.username == #studentId")
    public ResponseEntity<List<Award>> getAwardsByStudentId(@PathVariable String studentId) {
        List<Award> awards = awardService.getAwardsByStudentId(studentId);
        return ResponseEntity.ok(awards);
    }

    /**
     * 查询所有奖项
     * @return 奖项列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<?> getAwardsByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String awardName,
            @RequestParam(required = false) String awardType) {
        
        IPage<Award> awards = awardService.getAwardsByPage(pageNum, pageSize, null, awardName, null);
        
        // 转换为前端期望的格式
        return ResponseEntity.ok(new Object() {
            public final List<Award> records = awards.getRecords();
            public final long total = awards.getTotal();
        });
    }

    /**
     * 获取学生获奖次数
     * @param studentId 学生学号
     * @return 获奖次数
     */
    @GetMapping("/count/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or authentication.principal.username == #studentId")
    public ResponseEntity<Integer> getAwardCountByStudentId(@PathVariable String studentId) {
        int count = awardService.getAwardCountByStudentId(studentId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getAwardCount() {
        try {
            // 使用AwardService获取奖项总数
            long count = awardService.countAwards();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取最近的奖项
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Award>> getRecentAwards() {
        try {
            List<Award> awards = awardService.getRecentAwards();
            return ResponseEntity.ok(awards);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}