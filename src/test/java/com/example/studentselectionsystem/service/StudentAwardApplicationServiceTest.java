package com.example.studentselectionsystem.service;

import com.example.studentselectionsystem.entity.StudentAwardApplication;
import com.example.studentselectionsystem.service.impl.StudentAwardApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.studentselectionsystem.mapper.StudentAwardApplicationMapper;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentAwardApplicationServiceTest {

    @Mock
    private StudentAwardApplicationMapper studentAwardApplicationMapper;

    @InjectMocks
    private StudentAwardApplicationServiceImpl studentAwardApplicationService;

    private StudentAwardApplication application;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        application = new StudentAwardApplication();
        application.setId(1);
        application.setStudentId(1001L);
        application.setAwardId(1);
        application.setApplicationTime(new Date());
        application.setStatus(0); // 初始状态：待教师审核
        application.setTeacherApprovalStatus(0);
        application.setAdminApprovalStatus(0);
        application.setCreateTime(new Date());
        application.setUpdateTime(new Date());
    }

    @Test
    void testTeacherApproveApplication_Pass() {
        // 模拟从数据库获取申请
        when(studentAwardApplicationMapper.selectById(1)).thenReturn(application);
        when(studentAwardApplicationMapper.updateById(any(StudentAwardApplication.class))).thenReturn(1);

        // 测试教师审批通过
        StudentAwardApplication updatedApplication = studentAwardApplicationService.teacherApproveApplication(1, 1, "教师审批通过");

        // 验证结果
        assertNotNull(updatedApplication);
        assertEquals(1, updatedApplication.getTeacherApprovalStatus()); // 教师审批通过
        assertEquals(1, updatedApplication.getStatus()); // 整体状态：待管理员审批
        assertNotNull(updatedApplication.getTeacherApprovalTime());
        assertEquals("教师审批通过", updatedApplication.getTeacherApprovalComments());

        // 验证数据库操作
        verify(studentAwardApplicationMapper, times(1)).selectById(1);
        verify(studentAwardApplicationMapper, times(1)).updateById(updatedApplication);
    }

    @Test
    void testTeacherApproveApplication_Reject() {
        // 模拟从数据库获取申请
        when(studentAwardApplicationMapper.selectById(1)).thenReturn(application);
        when(studentAwardApplicationMapper.updateById(any(StudentAwardApplication.class))).thenReturn(1);

        // 测试教师审批拒绝
        StudentAwardApplication updatedApplication = studentAwardApplicationService.teacherApproveApplication(1, 2, "教师审批拒绝");

        // 验证结果
        assertNotNull(updatedApplication);
        assertEquals(2, updatedApplication.getTeacherApprovalStatus()); // 教师审批拒绝
        assertEquals(2, updatedApplication.getStatus()); // 整体状态：教师审核拒绝
        assertNotNull(updatedApplication.getTeacherApprovalTime());
        assertEquals("教师审批拒绝", updatedApplication.getTeacherApprovalComments());

        // 验证数据库操作
        verify(studentAwardApplicationMapper, times(1)).selectById(1);
        verify(studentAwardApplicationMapper, times(1)).updateById(updatedApplication);
    }

    @Test
    void testAdminApproveApplication_Pass() {
        // 设置初始状态为教师审批通过
        application.setStatus(1);
        application.setTeacherApprovalStatus(1);
        application.setTeacherApprovalTime(new Date());
        application.setTeacherApprovalComments("教师审批通过");

        // 模拟从数据库获取申请
        when(studentAwardApplicationMapper.selectById(1)).thenReturn(application);
        when(studentAwardApplicationMapper.updateById(any(StudentAwardApplication.class))).thenReturn(1);

        // 测试管理员审批通过
        StudentAwardApplication updatedApplication = studentAwardApplicationService.adminApproveApplication(1, 1, "管理员终审通过");

        // 验证结果
        assertNotNull(updatedApplication);
        assertEquals(1, updatedApplication.getAdminApprovalStatus()); // 管理员审批通过
        assertEquals(3, updatedApplication.getStatus()); // 整体状态：管理员审批通过
        assertNotNull(updatedApplication.getAdminApprovalTime());
        assertEquals("管理员终审通过", updatedApplication.getAdminApprovalComments());

        // 验证数据库操作
        verify(studentAwardApplicationMapper, times(1)).selectById(1);
        verify(studentAwardApplicationMapper, times(1)).updateById(updatedApplication);
    }

    @Test
    void testAdminApproveApplication_Reject() {
        // 设置初始状态为教师审批通过
        application.setStatus(1);
        application.setTeacherApprovalStatus(1);
        application.setTeacherApprovalTime(new Date());
        application.setTeacherApprovalComments("教师审批通过");

        // 模拟从数据库获取申请
        when(studentAwardApplicationMapper.selectById(1)).thenReturn(application);
        when(studentAwardApplicationMapper.updateById(any(StudentAwardApplication.class))).thenReturn(1);

        // 测试管理员审批拒绝
        StudentAwardApplication updatedApplication = studentAwardApplicationService.adminApproveApplication(1, 2, "管理员终审拒绝");

        // 验证结果
        assertNotNull(updatedApplication);
        assertEquals(2, updatedApplication.getAdminApprovalStatus()); // 管理员审批拒绝
        assertEquals(4, updatedApplication.getStatus()); // 整体状态：管理员审批拒绝
        assertNotNull(updatedApplication.getAdminApprovalTime());
        assertEquals("管理员终审拒绝", updatedApplication.getAdminApprovalComments());

        // 验证数据库操作
        verify(studentAwardApplicationMapper, times(1)).selectById(1);
        verify(studentAwardApplicationMapper, times(1)).updateById(updatedApplication);
    }

    @Test
    void testCreateApplication() {
        // 模拟数据库插入操作
        when(studentAwardApplicationMapper.insert(any(StudentAwardApplication.class))).thenReturn(1);

        // 测试创建申请
        StudentAwardApplication createdApplication = studentAwardApplicationService.createApplication(application);

        // 验证结果
        assertNotNull(createdApplication);
        assertEquals(0, createdApplication.getStatus()); // 初始状态：待教师审核
        assertNotNull(createdApplication.getApplicationTime());
        assertNotNull(createdApplication.getCreateTime());
        assertNotNull(createdApplication.getUpdateTime());

        // 验证数据库操作
        verify(studentAwardApplicationMapper, times(1)).insert(createdApplication);
    }

    @Test
    void testUpdateApplicationStatus() {
        // 模拟从数据库获取申请
        when(studentAwardApplicationMapper.selectById(1)).thenReturn(application);
        when(studentAwardApplicationMapper.updateById(any(StudentAwardApplication.class))).thenReturn(1);

        // 测试更新状态
        StudentAwardApplication updatedApplication = studentAwardApplicationService.updateApplicationStatus(1, 3);

        // 验证结果
        assertNotNull(updatedApplication);
        assertEquals(3, updatedApplication.getStatus()); // 状态已更新
        assertNotNull(updatedApplication.getUpdateTime());

        // 验证数据库操作
        verify(studentAwardApplicationMapper, times(1)).selectById(1);
        verify(studentAwardApplicationMapper, times(1)).updateById(updatedApplication);
    }
}
