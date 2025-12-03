package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Score;
import com.example.studentselectionsystem.repository.ScoreRepository;
import com.example.studentselectionsystem.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 成绩服务实现类
 */
@Service
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    /**
     * 创建成绩记录
     * @param score 成绩信息
     * @return 创建的成绩
     */
    @Override
    public Score createScore(Score score) {
        scoreRepository.insert(score);
        return score;
    }

    /**
     * 更新成绩信息
     * @param id 成绩ID
     * @param score 成绩信息
     * @return 更新后的成绩
     */
    @Override
    public Score updateScore(Integer id, Score score) {
        Score existingScore = scoreRepository.selectById(id);
        if (existingScore != null) {
            existingScore.setStudentId(score.getStudentId());
            existingScore.setCourseId(score.getCourseId());
            existingScore.setScore(score.getScore());
            existingScore.setSemester(score.getSemester());
            scoreRepository.updateById(existingScore);
            return existingScore;
        }
        return null;
    }

    /**
     * 删除成绩
     * @param id 成绩ID
     */
    @Override
    public void deleteScore(Integer id) {
        scoreRepository.deleteById(id);
    }

    /**
     * 根据ID查找成绩
     * @param id 成绩ID
     * @return 成绩信息
     */
    @Override
    public Optional<Score> findScoreById(Integer id) {
        return Optional.ofNullable(scoreRepository.selectById(id));
    }

    /**
     * 根据学生ID查找成绩
     * @param studentId 学生ID
     * @return 成绩列表
     */
    @Override
    public List<Score> findScoresByStudentId(Long studentId) {
        return scoreRepository.selectByStudentId(studentId);
    }

    /**
     * 根据课程ID查找成绩
     * @param courseId 课程ID
     * @return 成绩列表
     */
    @Override
    public List<Score> findScoresByCourseId(Integer courseId) {
        return scoreRepository.selectByCourseId(courseId);
    }

    /**
     * 根据学生ID和课程ID查找成绩
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 成绩信息
     */
    @Override
    public Optional<Score> findScoreByStudentIdAndCourseId(Long studentId, Integer courseId) {
        return scoreRepository.selectByStudentIdAndCourseId(studentId, courseId);
    }

    /**
     * 根据学生ID和学期查找成绩
     * @param studentId 学生ID
     * @param semester 学期
     * @return 成绩列表
     */
    @Override
    public List<Score> findScoresByStudentIdAndSemester(Long studentId, String semester) {
        return scoreRepository.selectByStudentIdAndSemester(studentId, semester);
    }

    /**
     * 根据课程ID和学期查找成绩
     * @param courseId 课程ID
     * @param semester 学期
     * @return 成绩列表
     */
    @Override
    public List<Score> findScoresByCourseIdAndSemester(Integer courseId, String semester) {
        return scoreRepository.selectByCourseIdAndSemester(courseId, semester);
    }

    /**
     * 根据学期查找成绩
     * @param semester 学期
     * @return 成绩列表
     */
    @Override
    public List<Score> findScoresBySemester(String semester) {
        return scoreRepository.selectBySemester(semester);
    }

    /**
     * 获取所有成绩
     * @return 成绩列表
     */
    @Override
    public List<Score> findAllScores() {
        return scoreRepository.selectList(null);
    }

    /**
     * 分页查询成绩
     * @param page 分页信息
     * @return 成绩列表
     */
    @Override
    public IPage<Score> findScoresByPage(IPage<Score> page) {
        return scoreRepository.selectPage(page, null);
    }

    @Override
    public IPage<Score> findScoresByPage(Integer current, Integer size) {
        // 创建MyBatis Plus分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Score> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        return scoreRepository.selectPage(page, null);
    }

    /**
     * 获取学生的课程成绩总和
     * @param studentId 学生ID
     * @return 总成绩
     */
    @Override
    public Double getTotalScoreByStudentId(Long studentId) {
        return scoreRepository.getTotalScoreByStudentId(studentId);
    }

    /**
     * 获取学生的课程成绩平均值
     * @param studentId 学生ID
     * @return 平均成绩
     */
    @Override
    public Double getAverageScoreByStudentId(Long studentId) {
        return scoreRepository.getAverageScoreByStudentId(studentId);
    }

    /**
     * 获取学生的课程成绩总和（按学期）
     * @param studentId 学生ID
     * @param semester 学期
     * @return 总成绩
     */
    @Override
    public Double getTotalScoreByStudentIdAndSemester(Long studentId, String semester) {
        return scoreRepository.getTotalScoreByStudentIdAndSemester(studentId, semester);
    }

    /**
     * 获取学生的课程成绩平均值（按学期）
     * @param studentId 学生ID
     * @param semester 学期
     * @return 平均成绩
     */
    @Override
    public Double getAverageScoreByStudentIdAndSemester(Long studentId, String semester) {
        return scoreRepository.getAverageScoreByStudentIdAndSemester(studentId, semester);
    }

    /**
     * 获取课程的平均成绩
     * @param courseId 课程ID
     * @return 平均成绩
     */
    @Override
    public Double getAverageScoreByCourseId(Integer courseId) {
        return scoreRepository.getAverageScoreByCourseId(courseId);
    }

    /**
     * 获取课程的平均成绩（按学期）
     * @param courseId 课程ID
     * @param semester 学期
     * @return 平均成绩
     */
    @Override
    public Double getAverageScoreByCourseIdAndSemester(Integer courseId, String semester) {
        return scoreRepository.getAverageScoreByCourseIdAndSemester(courseId, semester);
    }

    /**
     * 冒泡排序（降序）
     * @param scores 成绩列表
     * @return 排序后的成绩列表
     */
    @Override
    public List<Score> bubbleSortByScoreDesc(List<Score> scores) {
        List<Score> sortedScores = new ArrayList<>(scores);
        int n = sortedScores.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (sortedScores.get(j).getScore().compareTo(sortedScores.get(j + 1).getScore()) < 0) {
                    // 交换元素
                    Score temp = sortedScores.get(j);
                    sortedScores.set(j, sortedScores.get(j + 1));
                    sortedScores.set(j + 1, temp);
                }
            }
        }
        return sortedScores;
    }

    /**
     * 冒泡排序（升序）
     * @param scores 成绩列表
     * @return 排序后的成绩列表
     */
    @Override
    public List<Score> bubbleSortByScoreAsc(List<Score> scores) {
        List<Score> sortedScores = new ArrayList<>(scores);
        int n = sortedScores.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (sortedScores.get(j).getScore().compareTo(sortedScores.get(j + 1).getScore()) > 0) {
                    // 交换元素
                    Score temp = sortedScores.get(j);
                    sortedScores.set(j, sortedScores.get(j + 1));
                    sortedScores.set(j + 1, temp);
                }
            }
        }
        return sortedScores;
    }
}