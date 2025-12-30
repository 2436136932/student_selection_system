package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentselectionsystem.entity.Student;
import com.example.studentselectionsystem.entity.StudentAwardRecord;
import com.example.studentselectionsystem.entity.User;
import com.example.studentselectionsystem.service.StudentAwardRecordService;
import com.example.studentselectionsystem.service.StudentService;
import com.example.studentselectionsystem.service.UserService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 学生获奖记录控制器
 */
@RestController
@RequestMapping("/api/student-award-records")
@CrossOrigin(origins = "http://localhost:5173") // 允许来自前端的跨域请求
public class StudentAwardRecordController {

    @Autowired
    private StudentAwardRecordService studentAwardRecordService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private StudentService studentService;

    /**
     * 根据ID获取获奖记录
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<StudentAwardRecord> getRecordById(@PathVariable Long id) {
        StudentAwardRecord record = studentAwardRecordService.findRecordById(id);
        if (record == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(record, HttpStatus.OK);
    }

    /**
     * 根据学生ID获取获奖记录
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or (hasRole('STUDENT') and @userService.isCurrentUserStudent(#studentId))")
    public ResponseEntity<List<StudentAwardRecord>> getRecordsByStudentId(@PathVariable Long studentId) {
        List<StudentAwardRecord> records = studentAwardRecordService.findRecordsByStudentId(studentId);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    /**
     * 分页获取所有获奖记录
     */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<IPage<StudentAwardRecord>> getRecordsByPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<StudentAwardRecord> pageParam = new Page<>(page, size);
        IPage<StudentAwardRecord> recordsPage = studentAwardRecordService.findAllRecords(pageParam);
        return new ResponseEntity<>(recordsPage, HttpStatus.OK);
    }

    /**
     * 条件查询获奖记录
     */
    @GetMapping("/condition")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<IPage<StudentAwardRecord>> getRecordsByCondition(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long awardId,
            @RequestParam(required = false) String awardLevel,
            @RequestParam(required = false) String awardType,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) String awardName,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String major) {

        // 处理学生角色的特殊逻辑
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT") || a.getAuthority().equals("STUDENT"));
        
        if (isStudent) {
            // 如果是学生角色，通过用户ID获取对应的学生ID
            String username = authentication.getName();
            Optional<User> userOptional = userService.findUserByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Optional<Student> studentOptional = studentService.findStudentByUserId(user.getId());
                if (studentOptional.isPresent()) {
                    // 使用学生ID代替前端传递的user_id
                    studentId = studentOptional.get().getId();
                } else {
                    // 如果找不到对应的学生，返回空结果
                    Page<StudentAwardRecord> emptyPage = new Page<>(page, size);
                    emptyPage.setRecords(new ArrayList<>());
                    emptyPage.setTotal(0);
                    return new ResponseEntity<>(emptyPage, HttpStatus.OK);
                }
            }
        }

        Page<StudentAwardRecord> pageParam = new Page<>(page, size);
        IPage<StudentAwardRecord> recordsPage = studentAwardRecordService.findRecordsByCondition(
                pageParam, studentId, awardId, awardLevel, awardType, studentName, awardName, startTime, endTime, studentNumber, className, major);
        return new ResponseEntity<>(recordsPage, HttpStatus.OK);
    }

    /**
     * 导出获奖记录到Excel
     */
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public void exportRecords(HttpServletResponse response,
                              @RequestParam(required = false) Long studentId,
                              @RequestParam(required = false) Long awardId,
                              @RequestParam(required = false) String awardLevel,
                              @RequestParam(required = false) String awardType,
                              @RequestParam(required = false) String studentName,
                              @RequestParam(required = false) String awardName,
                              @RequestParam(required = false) String studentNumber,
                              @RequestParam(required = false) String className,
                              @RequestParam(required = false) String major,
                              @RequestParam(required = false) boolean isExportAll,
                              @RequestParam(required = false) String recordIds) throws IOException {

        // 处理学生角色的特殊逻辑
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT") || a.getAuthority().equals("STUDENT"));
        
        if (isStudent) {
            // 如果是学生角色，通过用户ID获取对应的学生ID
            String username = authentication.getName();
            Optional<User> userOptional = userService.findUserByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Optional<Student> studentOptional = studentService.findStudentByUserId(user.getId());
                if (studentOptional.isPresent()) {
                    // 使用学生ID代替前端传递的user_id
                    studentId = studentOptional.get().getId();
                } else {
                    // 如果找不到对应的学生，导出空记录
                    studentId = null;
                }
            }
        }

        // 设置查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("studentId", studentId);
        params.put("awardId", awardId);
        params.put("awardLevel", awardLevel);
        params.put("awardType", awardType);
        params.put("studentName", studentName);
        params.put("awardName", awardName);
        params.put("studentNumber", studentNumber);
        params.put("className", className);
        params.put("major", major);
        params.put("isExportAll", isExportAll);
        params.put("recordIds", recordIds);

        // 查询获奖记录
        List<StudentAwardRecord> records = studentAwardRecordService.findRecordsForExport(params);

        // 创建Excel工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("获奖记录");

        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"序号", "学生姓名", "学号", "班级", "专业", "奖项名称", "奖项级别", "奖项类型", "获奖时间"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            // 设置标题样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cell.setCellStyle(headerStyle);
        }

        // 填充数据行
        for (int i = 0; i < records.size(); i++) {
            StudentAwardRecord record = records.get(i);
            Row dataRow = sheet.createRow(i + 1);
            dataRow.createCell(0).setCellValue(i + 1);
            dataRow.createCell(1).setCellValue(record.getStudentName());
            dataRow.createCell(2).setCellValue(record.getStudentNumber());
            dataRow.createCell(3).setCellValue(record.getClassName());
            dataRow.createCell(4).setCellValue(record.getMajor());
            dataRow.createCell(5).setCellValue(record.getAwardName());
            dataRow.createCell(6).setCellValue(record.getAwardLevel());
            dataRow.createCell(7).setCellValue(record.getAwardType());
            dataRow.createCell(8).setCellValue(record.getAwardTime().toString());
        }

        // 设置列宽自适应
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=student_award_records.xlsx");

        // 输出Excel文件
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
