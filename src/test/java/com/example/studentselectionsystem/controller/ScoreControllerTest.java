package com.example.studentselectionsystem.controller;

import com.example.studentselectionsystem.entity.Score;
import com.example.studentselectionsystem.entity.Course;
import com.example.studentselectionsystem.service.ScoreService;
import com.example.studentselectionsystem.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 成绩控制器测试类
 */
public class ScoreControllerTest {

    @Mock
    private ScoreService scoreService;
    
    @Mock
    private CourseService courseService;

    @InjectMocks
    private ScoreController scoreController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(scoreController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testCreateScore_Success() throws Exception {
        // 准备测试数据
        Score score = new Score();
        score.setStudentId(4L); // 使用实际存在的学生ID
        score.setCourseId(1L);
        score.setUsualScore(BigDecimal.valueOf(85));
        score.setExamScore(BigDecimal.valueOf(90));
        score.setSemester("2024-2025秋季");

        Score createdScore = new Score();
        createdScore.setId(1L);
        createdScore.setStudentId(4L);
        createdScore.setCourseId(1L);
        createdScore.setUsualScore(BigDecimal.valueOf(85));
        createdScore.setExamScore(BigDecimal.valueOf(90));
        createdScore.setSemester("2024-2025秋季");

        // 模拟课程对象
        Course course = new Course();
        course.setId(1L);
        course.setName("计算机导论");
        course.setCode("CS101");

        // 模拟服务层行为
        when(courseService.findCourseById(1L)).thenReturn(Optional.of(course));
        when(scoreService.createScore(any(Score.class))).thenReturn(createdScore);

        // 执行测试
        mockMvc.perform(post("/api/scores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(score)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.studentId").value(4L))
                .andExpect(jsonPath("$.courseId").value(1L))
                .andExpect(jsonPath("$.usualScore").value(85))
                .andExpect(jsonPath("$.examScore").value(90));

        // 验证服务层方法被调用
        verify(courseService, times(1)).findCourseById(1L);
        verify(scoreService, times(1)).createScore(any(Score.class));
    }

    @Test
    public void testCreateScore_WithCourseCode() throws Exception {
        // 准备测试数据
        Score score = new Score();
        score.setStudentId(4L);
        score.setCourseCode("CS101");
        score.setUsualScore(BigDecimal.valueOf(85));
        score.setExamScore(BigDecimal.valueOf(90));
        score.setSemester("2024-2025秋季");

        // 模拟课程对象
        com.example.studentselectionsystem.entity.Course course = new com.example.studentselectionsystem.entity.Course();
        course.setId(1L);
        course.setName("计算机导论");
        course.setCode("CS101");

        Score createdScore = new Score();
        createdScore.setId(1L);
        createdScore.setStudentId(4L);
        createdScore.setCourseId(1L);
        createdScore.setUsualScore(BigDecimal.valueOf(85));
        createdScore.setExamScore(BigDecimal.valueOf(90));
        createdScore.setSemester("2024-2025秋季");

        // 模拟服务层行为
        when(courseService.findCourseByCode("CS101")).thenReturn(Optional.of(course));
        when(scoreService.createScore(any(Score.class))).thenReturn(createdScore);

        // 执行测试
        mockMvc.perform(post("/api/scores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(score)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.studentId").value(4L))
                .andExpect(jsonPath("$.courseId").value(1L));

        // 验证服务层方法被调用
        verify(courseService, times(1)).findCourseByCode("CS101");
        verify(scoreService, times(1)).createScore(any(Score.class));
    }
}