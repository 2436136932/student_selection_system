package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Score;
import com.example.studentselectionsystem.entity.Student;
import com.example.studentselectionsystem.entity.Course;
import com.example.studentselectionsystem.repository.ScoreRepository;
import com.example.studentselectionsystem.repository.StudentRepository;
import com.example.studentselectionsystem.repository.CourseRepository;
import com.example.studentselectionsystem.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 成绩服务实现类
 */
@Service
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    /**
     * 辅助方法：填充Score对象的学期、课程名称和学生姓名信息
     * @param score 需要填充信息的Score对象
     */
    private void populateScoreSemester(Score score) {
        if (score != null) {
            // 确保等级已经计算
            if (score.getTotalScore() != null && score.getGrade() == null) {
                score.setGrade(calculateGrade(score.getTotalScore()));
            }
            
            // 填充课程信息
            if (score.getCourseId() != null) {
                Course course = courseRepository.selectById(score.getCourseId());
                if (course != null) {
                    score.setSemester(course.getSemester());
                    score.setCourseName(course.getCourseName()); // 从课程表中获取课程名称
                    score.setCourseCode(course.getCourseCode()); // 从课程表中获取课程代码
                }
            }
            
            // 填充学生信息
            if (score.getStudentId() != null) {
                Student student = studentRepository.selectById(score.getStudentId());
                if (student != null) {
                    score.setStudentName(student.getName());
                    score.setStudentNumber(student.getStudentNumber());
                }
            }
            
            // 如果有学号但没有学生姓名，则根据学号查询
            if ((score.getStudentName() == null || score.getStudentName().isEmpty()) && 
                score.getStudentNumber() != null && !score.getStudentNumber().isEmpty()) {
                Optional<Student> studentOptional = studentRepository.selectByStudentId(score.getStudentNumber());
                if (studentOptional.isPresent()) {
                    Student student = studentOptional.get();
                    score.setStudentName(student.getName());
                    score.setStudentId(student.getId());
                }
            }
        }
    }
    
    /**
     * 辅助方法：填充Score列表的学期信息
     * @param scores 需要填充学期信息的Score列表
     */
    private void populateScoreSemesters(List<Score> scores) {
        if (scores != null) {
            for (Score score : scores) {
                populateScoreSemester(score);
            }
        }
    }
    
    /**
     * 辅助方法：根据总分计算等级
     * 等级划分规则：
     * - 90分及以上：A
     * - 80-89分：B
     * - 70-79分：C
     * - 60-69分：D
     * - 低于60分：F
     * @param totalScore 总分
     * @return 等级（A/B/C/D/F）
     */
    private String calculateGrade(BigDecimal totalScore) {
        if (totalScore == null) {
            return null;
        }
        int score = totalScore.intValue();
        if (score >= 90) {
            return "A";
        } else if (score >= 80 && score <= 89) {
            return "B";
        } else if (score >= 70 && score <= 79) {
            return "C";
        } else if (score >= 60 && score <= 69) {
            return "D";
        } else {
            return "F";
        }
    }

    /**
     * 创建成绩记录
     * @param score 成绩信息
     * @return 创建的成绩
     */
    @Override
    public Score createScore(Score score) {
        // 根据总分自动计算等级
        score.setGrade(calculateGrade(score.getTotalScore()));
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
    public Score updateScore(Long id, Score score) {
        Score existingScore = scoreRepository.selectById(id);
        if (existingScore != null) {
            existingScore.setStudentId(score.getStudentId());
            existingScore.setCourseId(score.getCourseId());
            existingScore.setUsualScore(score.getUsualScore());
            existingScore.setExamScore(score.getExamScore());
            existingScore.setTotalScore(score.getTotalScore());
            // 根据总分自动计算等级
            existingScore.setGrade(calculateGrade(score.getTotalScore()));
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
    public void deleteScore(Long id) {
        scoreRepository.deleteById(id);
    }

    /**
     * 根据ID查找成绩
     * @param id 成绩ID
     * @return 成绩信息
     */
    @Override
    public Optional<Score> findScoreById(Long id) {
        Score score = scoreRepository.selectById(id);
        if (score != null) {
            populateScoreSemester(score);
        }
        return Optional.ofNullable(score);
    }

    /**
     * 根据学生ID查找成绩
     * @param studentId 学生ID
     * @return 成绩列表
     */
    @Override
    public List<Score> findScoresByStudentId(Long studentId) {
        List<Score> scores = scoreRepository.selectByStudentId(studentId);
        populateScoreSemesters(scores);
        return scores;
    }

    /**
     * 根据课程ID查找成绩
     * @param courseId 课程ID
     * @return 成绩列表
     */
    @Override
    public List<Score> findScoresByCourseId(Long courseId) {
        List<Score> scores = scoreRepository.selectByCourseId(courseId);
        populateScoreSemesters(scores);
        return scores;
    }

    /**
     * 根据学生ID和课程ID查找成绩
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 成绩信息
     */
    @Override
    public Optional<Score> findScoreByStudentIdAndCourseId(Long studentId, Long courseId) {
        Optional<Score> scoreOptional = scoreRepository.selectByStudentIdAndCourseId(studentId, courseId);
        scoreOptional.ifPresent(this::populateScoreSemester);
        return scoreOptional;
    }

    /**
     * 根据学生ID和学期查找成绩
     * @param studentId 学生ID
     * @param semester 学期
     * @return 成绩列表
     */
    @Override
    public List<Score> findScoresByStudentIdAndSemester(Long studentId, String semester) {
        List<Score> scores = scoreRepository.selectByStudentIdAndSemester(studentId, semester);
        populateScoreSemesters(scores);
        return scores;
    }

    /**
     * 根据课程ID和学期查找成绩
     * @param courseId 课程ID
     * @param semester 学期
     * @return 成绩列表
     */
    @Override
    public List<Score> findScoresByCourseIdAndSemester(Long courseId, String semester) {
        List<Score> scores = scoreRepository.selectByCourseIdAndSemester(courseId, semester);
        populateScoreSemesters(scores);
        return scores;
    }

    /**
     * 根据学期查找成绩
     * @param semester 学期
     * @return 成绩列表
     */
    @Override
    public List<Score> findScoresBySemester(String semester) {
        List<Score> scores = scoreRepository.selectBySemester(semester);
        populateScoreSemesters(scores);
        return scores;
    }

    /**
     * 获取所有成绩
     * @return 成绩列表
     */
    @Override
    public List<Score> findAllScores() {
        List<Score> scores = scoreRepository.selectList(null);
        populateScoreSemesters(scores);
        return scores;
    }

    /**
     * 分页查询成绩
     * @param page 分页信息
     * @return 成绩列表
     */
    @Override
    public IPage<Score> findScoresByPage(IPage<Score> page) {
        IPage<Score> scorePage = scoreRepository.selectPage(page, null);
        populateScoreSemesters(scorePage.getRecords());
        return scorePage;
    }

    @Override
    public IPage<Score> findScoresByPage(Integer current, Integer size) {
        // 创建MyBatis Plus分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Score> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        IPage<Score> scorePage = scoreRepository.selectPage(page, null);
        populateScoreSemesters(scorePage.getRecords());
        return scorePage;
    }

    @Override
    public IPage<Score> findScoresByPage(Integer current, Integer size, String studentNumber, String courseCode, String courseName) {
        // 创建MyBatis Plus分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Score> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        
        // 构建查询条件
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        boolean hasStudentCondition = false;
        
        // 处理学生学号查询
        if (studentNumber != null && !studentNumber.isEmpty()) {
            // 先根据学号查询学生ID
            QueryWrapper<Student> studentWrapper = new QueryWrapper<>();
            studentWrapper.eq("student_number", studentNumber);
            Student student = studentRepository.selectOne(studentWrapper);
            if (student != null) {
                queryWrapper.eq("student_id", student.getId());
                hasStudentCondition = true;
            } else {
                // 如果根据学号查询不到学生信息，返回空结果
                System.out.println("根据学号查询不到学生信息: " + studentNumber);
                return page;
            }
        }
        
        // 处理课程代码查询
        if (courseCode != null && !courseCode.isEmpty()) {
            // 先根据课程代码查询课程ID
            QueryWrapper<Course> courseWrapper = new QueryWrapper<>();
            courseWrapper.eq("course_code", courseCode);
            Course course = courseRepository.selectOne(courseWrapper);
            if (course != null) {
                queryWrapper.eq("course_id", course.getId());
            }
        }
        
        // 处理课程名称查询
        if (courseName != null && !courseName.isEmpty()) {
            // 先根据课程名称查询课程列表
            QueryWrapper<Course> courseWrapper = new QueryWrapper<>();
            courseWrapper.like("course_name", courseName);
            List<Course> courses = courseRepository.selectList(courseWrapper);
            if (!courses.isEmpty()) {
                // 获取课程ID列表
                List<Long> courseIds = courses.stream().map(Course::getId).collect(java.util.stream.Collectors.toList());
                queryWrapper.in("course_id", courseIds);
            } else {
                // 如果根据课程名称查询不到课程信息，返回空结果
                System.out.println("根据课程名称查询不到课程信息: " + courseName);
                return page;
            }
        }
        
        // 查询成绩数据
        IPage<Score> scorePage = scoreRepository.selectPage(page, queryWrapper);
        
        // 填充学生信息和课程信息
        populateScoreSemesters(scorePage.getRecords());
        
        System.out.println("findScoresByPage查询结果: " + scorePage.getTotal() + "条记录");
        return scorePage;
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
    public Double getAverageScoreByCourseId(Long courseId) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        List<Score> scores = scoreRepository.selectList(queryWrapper);
        if (scores.isEmpty()) {
            return 0.0;
        }
        
        BigDecimal sum = BigDecimal.ZERO;
        int validCount = 0;
        
        for (Score score : scores) {
            BigDecimal totalScore = score.getTotalScore();
            if (totalScore != null) {
                sum = sum.add(totalScore);
                validCount++;
            }
        }
        
        if (validCount == 0) {
            return 0.0;
        }
        
        // 保留两位小数，四舍五入
        BigDecimal average = sum.divide(BigDecimal.valueOf(validCount), 2, RoundingMode.HALF_UP);
        return average.doubleValue();
    }

    /**
     * 获取课程的平均成绩（按学期）
     * @param courseId 课程ID
     * @param semester 学期
     * @return 平均成绩
     */
    @Override
    public Double getAverageScoreByCourseIdAndSemester(Long courseId, String semester) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId)
                .eq("semester", semester);
        List<Score> scores = scoreRepository.selectList(queryWrapper);
        if (scores.isEmpty()) {
            return 0.0;
        }
        
        BigDecimal sum = BigDecimal.ZERO;
        int validCount = 0;
        
        for (Score score : scores) {
            BigDecimal totalScore = score.getTotalScore();
            if (totalScore != null) {
                sum = sum.add(totalScore);
                validCount++;
            }
        }
        
        if (validCount == 0) {
            return 0.0;
        }
        
        // 保留两位小数，四舍五入
        BigDecimal average = sum.divide(BigDecimal.valueOf(validCount), 2, RoundingMode.HALF_UP);
        return average.doubleValue();
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
                if (sortedScores.get(j).getTotalScore().compareTo(sortedScores.get(j + 1).getTotalScore()) < 0) {
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
                if (sortedScores.get(j).getTotalScore().compareTo(sortedScores.get(j + 1).getTotalScore()) > 0) {
                    // 交换元素
                    Score temp = sortedScores.get(j);
                    sortedScores.set(j, sortedScores.get(j + 1));
                    sortedScores.set(j + 1, temp);
                }
            }
        }
        return sortedScores;
    }
    
    @Override
    public Map<String, Object> getScoreStatistics(String studentNumber, String courseCode, String semester) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 构建查询条件
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        
        // 处理学生学号查询
        if (studentNumber != null && !studentNumber.isEmpty()) {
            // 先根据学号查询学生ID
            QueryWrapper<Student> studentWrapper = new QueryWrapper<>();
            studentWrapper.eq("student_number", studentNumber);
            Student student = studentRepository.selectOne(studentWrapper);
            if (student != null) {
                queryWrapper.eq("student_id", student.getId());
            }
        }
        
        // 处理课程代码查询
        if (courseCode != null && !courseCode.isEmpty()) {
            // 先根据课程代码查询课程ID
            QueryWrapper<Course> courseWrapper = new QueryWrapper<>();
            courseWrapper.eq("course_code", courseCode);
            Course course = courseRepository.selectOne(courseWrapper);
            if (course != null) {
                queryWrapper.eq("course_id", course.getId());
            }
        }
        
        // 处理学期查询
        if (semester != null && !semester.isEmpty()) {
            queryWrapper.like("semester", semester);
        }
        
        // 查询成绩数据
        List<Score> scores = scoreRepository.selectList(queryWrapper);
        populateScoreSemesters(scores);
        
        // 计算统计数据
        int totalCount = scores.size();
        int validCount = 0;
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal maxScore = BigDecimal.ZERO;
        BigDecimal minScore = BigDecimal.valueOf(100);
        
        // 各等级人数
        int aCount = 0;
        int bCount = 0;
        int cCount = 0;
        int dCount = 0;
        int fCount = 0;
        
        for (Score score : scores) {
            BigDecimal totalScore = score.getTotalScore();
            if (totalScore != null) {
                validCount++;
                sum = sum.add(totalScore);
                
                // 更新最高分
                if (totalScore.compareTo(maxScore) > 0) {
                    maxScore = totalScore;
                }
                
                // 更新最低分
                if (totalScore.compareTo(minScore) < 0) {
                    minScore = totalScore;
                }
                
                // 统计各等级人数
                String grade = score.getGrade();
                if (grade == null) {
                    grade = calculateGrade(totalScore);
                }
                
                switch (grade) {
                    case "A":
                        aCount++;
                        break;
                    case "B":
                        bCount++;
                        break;
                    case "C":
                        cCount++;
                        break;
                    case "D":
                        dCount++;
                        break;
                    case "F":
                        fCount++;
                        break;
                }
            }
        }
        
        // 计算平均分
        BigDecimal averageScore = BigDecimal.ZERO;
        if (validCount > 0) {
            averageScore = sum.divide(BigDecimal.valueOf(validCount), 2, RoundingMode.HALF_UP);
        }
        
        // 填充统计数据
        statistics.put("totalCount", totalCount);
        statistics.put("validCount", validCount);
        statistics.put("averageScore", averageScore.doubleValue());
        statistics.put("maxScore", maxScore.doubleValue());
        statistics.put("minScore", minScore.doubleValue());
        
        // 各等级人数
        Map<String, Integer> gradeCount = new HashMap<>();
        gradeCount.put("A", aCount);
        gradeCount.put("B", bCount);
        gradeCount.put("C", cCount);
        gradeCount.put("D", dCount);
        gradeCount.put("F", fCount);
        statistics.put("gradeCount", gradeCount);
        
        return statistics;
    }
}