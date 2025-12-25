package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentselectionsystem.entity.Award;
import com.example.studentselectionsystem.entity.Student;
import com.example.studentselectionsystem.entity.StudentAwardApplication;
import com.example.studentselectionsystem.entity.StudentAwardRecord;
import com.example.studentselectionsystem.mapper.AwardMapper;
import com.example.studentselectionsystem.mapper.StudentAwardApplicationMapper;
import com.example.studentselectionsystem.mapper.StudentAwardRecordMapper;
import com.example.studentselectionsystem.mapper.StudentMapper;
import com.example.studentselectionsystem.service.StudentAwardRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学生获奖记录服务实现类
 */
@Service
@Transactional
public class StudentAwardRecordServiceImpl extends ServiceImpl<StudentAwardRecordMapper, StudentAwardRecord> implements StudentAwardRecordService {

    @Autowired
    private StudentAwardRecordMapper studentAwardRecordMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private AwardMapper awardMapper;

    @Autowired
    private StudentAwardApplicationMapper studentAwardApplicationMapper;

    @Override
    public StudentAwardRecord createRecord(StudentAwardRecord record) {
        record.setCreatedAt(new Date());
        record.setUpdateTime(new Date());
        studentAwardRecordMapper.insert(record);
        return record;
    }

    @Override
    public StudentAwardRecord findRecordById(Long id) {
        return studentAwardRecordMapper.selectById(id);
    }

    @Override
    public List<StudentAwardRecord> findRecordsByStudentId(Long studentId) {
        LambdaQueryWrapper<StudentAwardRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentAwardRecord::getStudentId, studentId);
        return studentAwardRecordMapper.selectList(wrapper);
    }

    @Override
    public IPage<StudentAwardRecord> findAllRecords(Page<StudentAwardRecord> page) {
        return studentAwardRecordMapper.selectPage(page, null);
    }

    @Override
    public IPage<StudentAwardRecord> findRecordsByCondition(Page<StudentAwardRecord> page, Long studentId, Long awardId, 
                                                         String awardLevel, String awardType, String studentName, String awardName,
                                                         String startTime, String endTime, String studentNumber, String className, String major) {
        LambdaQueryWrapper<StudentAwardRecord> wrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        if (studentId != null) {
            wrapper.eq(StudentAwardRecord::getStudentId, studentId);
        }
        if (awardId != null) {
            wrapper.eq(StudentAwardRecord::getAwardId, awardId);
        }
        if (awardLevel != null && !awardLevel.isEmpty()) {
            wrapper.eq(StudentAwardRecord::getAwardLevel, awardLevel);
        }
        if (awardType != null && !awardType.isEmpty()) {
            wrapper.eq(StudentAwardRecord::getAwardType, awardType);
        }
        if (studentName != null && !studentName.isEmpty()) {
            wrapper.like(StudentAwardRecord::getStudentName, studentName);
        }
        if (awardName != null && !awardName.isEmpty()) {
            wrapper.like(StudentAwardRecord::getAwardName, awardName);
        }
        if (studentNumber != null && !studentNumber.isEmpty()) {
            wrapper.like(StudentAwardRecord::getStudentNumber, studentNumber);
        }
        if (className != null && !className.isEmpty()) {
            wrapper.like(StudentAwardRecord::getClassName, className);
        }
        if (major != null && !major.isEmpty()) {
            wrapper.like(StudentAwardRecord::getMajor, major);
        }
        // 添加时间范围查询条件
        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge(StudentAwardRecord::getAwardTime, startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(StudentAwardRecord::getAwardTime, endTime);
        }

        // 按获奖时间倒序排序
        wrapper.orderByDesc(StudentAwardRecord::getAwardTime);

        return studentAwardRecordMapper.selectPage(page, wrapper);
    }

    @Override
    public List<StudentAwardRecord> findRecordsForExport(Map<String, Object> params) {
        LambdaQueryWrapper<StudentAwardRecord> wrapper = new LambdaQueryWrapper<>();

        // 检查是否为导出全部
        boolean isExportAll = true;
        if (params.containsKey("isExportAll")) {
            isExportAll = (boolean) params.get("isExportAll");
        }

        // 如果不是导出全部，检查是否有recordIds参数
        if (!isExportAll && params.containsKey("recordIds") && params.get("recordIds") != null) {
            String recordIds = params.get("recordIds").toString();
            if (!recordIds.isEmpty()) {
                // 将recordIds字符串分割为Long类型的ID列表
                List<Long> idList = Arrays.stream(recordIds.split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                // 添加ID列表查询条件
                wrapper.in(StudentAwardRecord::getId, idList);
                // 按获奖时间倒序排序
                wrapper.orderByDesc(StudentAwardRecord::getAwardTime);
                return studentAwardRecordMapper.selectList(wrapper);
            }
        }

        // 导出全部或没有指定ID列表时，使用原有查询条件
        // 添加查询条件
        if (params.containsKey("studentId") && params.get("studentId") != null) {
            wrapper.eq(StudentAwardRecord::getStudentId, params.get("studentId"));
        }
        if (params.containsKey("awardId") && params.get("awardId") != null) {
            wrapper.eq(StudentAwardRecord::getAwardId, params.get("awardId"));
        }
        if (params.containsKey("awardLevel") && params.get("awardLevel") != null && !params.get("awardLevel").toString().isEmpty()) {
            wrapper.eq(StudentAwardRecord::getAwardLevel, params.get("awardLevel"));
        }
        if (params.containsKey("awardType") && params.get("awardType") != null && !params.get("awardType").toString().isEmpty()) {
            wrapper.eq(StudentAwardRecord::getAwardType, params.get("awardType"));
        }
        if (params.containsKey("studentName") && params.get("studentName") != null && !params.get("studentName").toString().isEmpty()) {
            wrapper.like(StudentAwardRecord::getStudentName, params.get("studentName"));
        }
        if (params.containsKey("awardName") && params.get("awardName") != null && !params.get("awardName").toString().isEmpty()) {
            wrapper.like(StudentAwardRecord::getAwardName, params.get("awardName"));
        }
        if (params.containsKey("studentNumber") && params.get("studentNumber") != null && !params.get("studentNumber").toString().isEmpty()) {
            wrapper.like(StudentAwardRecord::getStudentNumber, params.get("studentNumber"));
        }
        if (params.containsKey("className") && params.get("className") != null && !params.get("className").toString().isEmpty()) {
            wrapper.like(StudentAwardRecord::getClassName, params.get("className"));
        }
        if (params.containsKey("major") && params.get("major") != null && !params.get("major").toString().isEmpty()) {
            wrapper.like(StudentAwardRecord::getMajor, params.get("major"));
        }

        // 按获奖时间倒序排序
        wrapper.orderByDesc(StudentAwardRecord::getAwardTime);

        return studentAwardRecordMapper.selectList(wrapper);
    }

    @Override
    public void deleteRecordsByStudentId(Long studentId) {
        LambdaQueryWrapper<StudentAwardRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentAwardRecord::getStudentId, studentId);
        studentAwardRecordMapper.delete(wrapper);
    }

    @Override
    public void deleteRecord(Long id) {
        studentAwardRecordMapper.deleteById(id);
    }

    @Override
    public StudentAwardRecord createRecordFromApplication(Long applicationId) {
        // 查询申请信息
        StudentAwardApplication application = studentAwardApplicationMapper.selectById(applicationId);
        if (application == null) {
            return null;
        }

        // 查询学生信息
        Student student = studentMapper.selectById(application.getStudentId());
        if (student == null) {
            return null;
        }

        // 查询奖项信息
        Award award = awardMapper.selectById(application.getAwardId());
        if (award == null) {
            return null;
        }

        // 创建获奖记录
        StudentAwardRecord record = new StudentAwardRecord();
        record.setStudentId(student.getId());
        record.setAwardId(award.getId());
        record.setStudentName(student.getName());
        record.setStudentNumber(student.getStudentNumber());
        record.setClassName(student.getClassName());
        record.setMajor(student.getMajor());
        record.setAwardName(award.getAwardName());
        record.setAwardLevel(award.getAwardLevel());
        record.setAwardType(award.getAwardType());
        record.setAwardTime(new Date());
        record.setCreatedAt(new Date());
        record.setUpdateTime(new Date());

        studentAwardRecordMapper.insert(record);
        return record;
    }
}
