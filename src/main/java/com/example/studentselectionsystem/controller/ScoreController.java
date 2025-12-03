package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Score;
import com.example.studentselectionsystem.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 成绩控制器
 */
@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    /**
     * 创建成绩记录
     * @param score 成绩信息
     * @return 创建的成绩
     */
    @PostMapping
    @PreAuthorize("hasRole('admin') or hasRole('teacher')")
    public ResponseEntity<Score> createScore(@RequestBody Score score) {
        try {
            Score createdScore = scoreService.createScore(score);
            return new ResponseEntity<>(createdScore, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 更新成绩信息
     * @param id 成绩ID
     * @param score 成绩信息
     * @return 更新后的成绩
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('teacher')")
    public ResponseEntity<Score> updateScore(@PathVariable("id") Integer id, @RequestBody Score score) {
        try {
            Score updatedScore = scoreService.updateScore(id, score);
            if (updatedScore != null) {
                return new ResponseEntity<>(updatedScore, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除成绩
     * @param id 成绩ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('teacher')")
    public ResponseEntity<HttpStatus> deleteScore(@PathVariable("id") Integer id) {
        try {
            scoreService.deleteScore(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据ID查找成绩
     * @param id 成绩ID
     * @return 成绩信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('teacher')")
    public ResponseEntity<Score> findScoreById(@PathVariable("id") Integer id) {
        Optional<Score> score = scoreService.findScoreById(id);
        if (score.isPresent()) {
            return new ResponseEntity<>(score.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 根据学生ID查找成绩
     * @param studentId 学生ID
     * @return 成绩列表
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('admin') or hasRole('teacher') or authentication.principal.username == #studentId")
    public ResponseEntity<List<Score>> findScoresByStudentId(@PathVariable("studentId") Long studentId) {
        try {
            List<Score> scores = scoreService.findScoresByStudentId(studentId);
            if (scores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(scores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据课程ID查找成绩
     * @param courseId 课程ID
     * @return 成绩列表
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Score>> findScoresByCourseId(@PathVariable("courseId") Integer courseId) {
        try {
            List<Score> scores = scoreService.findScoresByCourseId(courseId);
            if (scores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(scores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据学生ID和课程ID查找成绩
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 成绩信息
     */
    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<Score> findScoreByStudentIdAndCourseId(
            @PathVariable("studentId") Long studentId,
            @PathVariable("courseId") Integer courseId) {
        Optional<Score> score = scoreService.findScoreByStudentIdAndCourseId(studentId, courseId);
        if (score.isPresent()) {
            return new ResponseEntity<>(score.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 根据学生ID和学期查找成绩
     * @param studentId 学生ID
     * @param semester 学期
     * @return 成绩列表
     */
    @GetMapping("/student/{studentId}/semester/{semester}")
    public ResponseEntity<List<Score>> findScoresByStudentIdAndSemester(
            @PathVariable("studentId") Long studentId,
            @PathVariable("semester") String semester) {
        try {
            List<Score> scores = scoreService.findScoresByStudentIdAndSemester(studentId, semester);
            if (scores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(scores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据课程ID和学期查找成绩
     * @param courseId 课程ID
     * @param semester 学期
     * @return 成绩列表
     */
    @GetMapping("/course/{courseId}/semester/{semester}")
    public ResponseEntity<List<Score>> findScoresByCourseIdAndSemester(
            @PathVariable("courseId") Integer courseId,
            @PathVariable("semester") String semester) {
        try {
            List<Score> scores = scoreService.findScoresByCourseIdAndSemester(courseId, semester);
            if (scores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(scores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据学期查找成绩
     * @param semester 学期
     * @return 成绩列表
     */
    @GetMapping("/semester/{semester}")
    public ResponseEntity<List<Score>> findScoresBySemester(@PathVariable("semester") String semester) {
        try {
            List<Score> scores = scoreService.findScoresBySemester(semester);
            if (scores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(scores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取所有成绩
     * @return 成绩列表
     */
    @GetMapping
    public ResponseEntity<List<Score>> findAllScores() {
        try {
            List<Score> scores = scoreService.findAllScores();
            if (scores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(scores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 分页获取成绩
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @return 成绩分页列表
     */
    @GetMapping("/page")
    public ResponseEntity<IPage<Score>> findScoresByPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            IPage<Score> scores = scoreService.findScoresByPage(current, size);
            return new ResponseEntity<>(scores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取学生的课程成绩总和
     * @param studentId 学生ID
     * @return 总成绩
     */
    @GetMapping("/student/{studentId}/total")
    public ResponseEntity<Double> getTotalScoreByStudentId(@PathVariable("studentId") Long studentId) {
        try {
            Double totalScore = scoreService.getTotalScoreByStudentId(studentId);
            return new ResponseEntity<>(totalScore, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取学生的课程成绩平均值
     * @param studentId 学生ID
     * @return 平均成绩
     */
    @GetMapping("/student/{studentId}/average")
    public ResponseEntity<Double> getAverageScoreByStudentId(@PathVariable("studentId") Long studentId) {
        try {
            Double averageScore = scoreService.getAverageScoreByStudentId(studentId);
            return new ResponseEntity<>(averageScore, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取学生的课程成绩总和（按学期）
     * @param studentId 学生ID
     * @param semester 学期
     * @return 总成绩
     */
    @GetMapping("/student/{studentId}/semester/{semester}/total")
    public ResponseEntity<Double> getTotalScoreByStudentIdAndSemester(
            @PathVariable("studentId") Long studentId,
            @PathVariable("semester") String semester) {
        try {
            Double totalScore = scoreService.getTotalScoreByStudentIdAndSemester(studentId, semester);
            return new ResponseEntity<>(totalScore, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取学生的课程成绩平均值（按学期）
     * @param studentId 学生ID
     * @param semester 学期
     * @return 平均成绩
     */
    @GetMapping("/student/{studentId}/semester/{semester}/average")
    public ResponseEntity<Double> getAverageScoreByStudentIdAndSemester(
            @PathVariable("studentId") Long studentId,
            @PathVariable("semester") String semester) {
        try {
            Double averageScore = scoreService.getAverageScoreByStudentIdAndSemester(studentId, semester);
            return new ResponseEntity<>(averageScore, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取课程的平均成绩
     * @param courseId 课程ID
     * @return 平均成绩
     */
    @GetMapping("/course/{courseId}/average")
    public ResponseEntity<Double> getAverageScoreByCourseId(@PathVariable("courseId") Integer courseId) {
        try {
            Double averageScore = scoreService.getAverageScoreByCourseId(courseId);
            return new ResponseEntity<>(averageScore, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取课程的平均成绩（按学期）
     * @param courseId 课程ID
     * @param semester 学期
     * @return 平均成绩
     */
    @GetMapping("/course/{courseId}/semester/{semester}/average")
    public ResponseEntity<Double> getAverageScoreByCourseIdAndSemester(
            @PathVariable("courseId") Integer courseId,
            @PathVariable("semester") String semester) {
        try {
            Double averageScore = scoreService.getAverageScoreByCourseIdAndSemester(courseId, semester);
            return new ResponseEntity<>(averageScore, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 使用冒泡排序算法按成绩降序排序
     * @return 排序后的成绩列表
     */
    @GetMapping("/sorted/desc")
    public ResponseEntity<List<Score>> bubbleSortByScoreDesc() {
        try {
            List<Score> scores = scoreService.findAllScores();
            List<Score> sortedScores = scoreService.bubbleSortByScoreDesc(scores);
            if (sortedScores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(sortedScores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 使用冒泡排序算法按成绩升序排序
     * @return 排序后的成绩列表
     */
    @GetMapping("/sorted/asc")
    public ResponseEntity<List<Score>> bubbleSortByScoreAsc() {
        try {
            List<Score> scores = scoreService.findAllScores();
            List<Score> sortedScores = scoreService.bubbleSortByScoreAsc(scores);
            if (sortedScores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(sortedScores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}