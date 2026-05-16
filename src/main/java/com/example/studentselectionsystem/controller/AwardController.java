package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Award;
import com.example.studentselectionsystem.service.AwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.InputStreamResource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 评奖评优Controller
 */
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
        award.setId(Long.valueOf(id));
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
    
    /**
     * 发布奖项
     * @param id 奖项ID
     * @return 响应结果
     */
    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> publishAward(@PathVariable Integer id) {
        boolean success = awardService.publishAward(String.valueOf(id));
        if (success) {
            return ResponseEntity.ok("奖项发布成功");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 下载导入模板（Excel）
     */
    @GetMapping("/template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> downloadTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("awards");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("awardName");
            header.createCell(1).setCellValue("awardLevel");
            header.createCell(2).setCellValue("awardType");
            header.createCell(3).setCellValue("startTime (yyyy-MM-dd HH:mm:ss)");
            header.createCell(4).setCellValue("endTime (yyyy-MM-dd HH:mm:ss)");
            header.createCell(5).setCellValue("quota");
            header.createCell(6).setCellValue("description");
            header.createCell(7).setCellValue("requirement");
            header.createCell(8).setCellValue("status (未发布/已发布/已结束)");
            header.createCell(9).setCellValue("approvingTeacherId (可选)");
            header.createCell(10).setCellValue("approvingTeacherName (可选)");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=award_template.xlsx");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(in));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 批量导入奖项（Excel）
     */
    @PostMapping("/batch-import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> batchImport(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("上传文件为空");
        }

        try (InputStream in = file.getInputStream()) {
            Workbook workbook;
            String fname = file.getOriginalFilename();
            if (fname != null && fname.toLowerCase().endsWith(".xls")) {
                workbook = new HSSFWorkbook(in);
            } else {
                workbook = new XSSFWorkbook(in);
            }

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return ResponseEntity.badRequest().body("Excel内容为空");
            }

            int successCount = 0;
            StringBuilder errors = new StringBuilder();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String awardName = getCellString(row.getCell(0));
                    if (awardName == null || awardName.isEmpty()) {
                        errors.append("第").append(i+1).append("行: 奖项名称不能为空; ");
                        continue;
                    }
                    Award award = new Award();
                    award.setAwardName(awardName);
                    award.setAwardLevel(getCellString(row.getCell(1)));
                    award.setAwardType(getCellString(row.getCell(2)));
                    String start = getCellString(row.getCell(3));
                    String end = getCellString(row.getCell(4));
                    if (start != null && !start.isEmpty()) {
                        award.setStartTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start));
                    }
                    if (end != null && !end.isEmpty()) {
                        award.setEndTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end));
                    }
                    String quotaStr = getCellString(row.getCell(5));
                    if (quotaStr != null && !quotaStr.isEmpty()) {
                        award.setQuota(Integer.parseInt(quotaStr));
                    }
                    award.setDescription(getCellString(row.getCell(6)));
                    award.setRequirement(getCellString(row.getCell(7)));
                    String status = getCellString(row.getCell(8));
                    award.setStatus(status == null || status.isEmpty() ? "未发布" : status);
                    String teacherIdStr = getCellString(row.getCell(9));
                    if (teacherIdStr != null && !teacherIdStr.isEmpty()) {
                        award.setApprovingTeacherId(Long.parseLong(teacherIdStr));
                    }
                    award.setApprovingTeacherName(getCellString(row.getCell(10)));

                    award.setCreateTime(new Date());
                    award.setUpdateTime(new Date());

                    boolean ok = awardService.addAward(award);
                    if (ok) successCount++; else errors.append("第").append(i+1).append("行新增失败; ");
                } catch (Exception ex) {
                    errors.append("第").append(i+1).append("行解析失败: ").append(ex.getMessage()).append("; ");
                }
            }

            String msg = "导入完成: 成功 " + successCount + " 条";
            if (errors.length() > 0) msg += ", 部分失败: " + errors.toString();
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("导入出错: " + e.getMessage());
        }
    }

    private String getCellString(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            double val = cell.getNumericCellValue();
            if (val == Math.floor(val) && !Double.isInfinite(val)) {
                return String.valueOf((long) val);
            }
            return String.valueOf(val);
        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}
