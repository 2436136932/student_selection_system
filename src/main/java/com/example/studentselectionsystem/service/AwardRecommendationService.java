package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.studentselectionsystem.entity.*;
import com.example.studentselectionsystem.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AwardRecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(AwardRecommendationService.class);

    @Autowired
    private AwardMapper awardMapper;

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private StudentAwardRecordMapper studentAwardRecordMapper;

    @Autowired
    private StudentAwardApplicationMapper studentAwardApplicationMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private RecommendationWeightService recommendationWeightService;

    public List<Map<String, Object>> recommendAwards(Long studentId) {
        try {
            Student student = studentMapper.selectById(studentId);
            if (student == null) {
                return Collections.emptyList();
            }

            List<Award> availableAwards = awardMapper.selectList(
                new LambdaQueryWrapper<Award>()
                    .eq(Award::getStatus, "已发布")
                    .eq(Award::getCurrentStatus, "进行中")
            );

            List<Score> studentScores = scoreMapper.selectList(
                new LambdaQueryWrapper<Score>()
                    .eq(Score::getStudentId, studentId)
            );

            List<StudentAwardRecord> studentAwardRecords = studentAwardRecordMapper.selectList(
                new LambdaQueryWrapper<StudentAwardRecord>()
                    .eq(StudentAwardRecord::getStudentId, studentId)
            );

            List<Map<String, Object>> recommendations = new ArrayList<>();

            for (Award award : availableAwards) {
                boolean alreadyApplied = studentAwardApplicationMapper.exists(
                    new LambdaQueryWrapper<StudentAwardApplication>()
                        .eq(StudentAwardApplication::getStudentId, studentId)
                        .eq(StudentAwardApplication::getAwardId, award.getId())
                );

                if (alreadyApplied) {
                    continue;
                }

                double matchScore = calculateMatchScore(student, award, studentScores, studentAwardRecords);
                
                double winProbability = calculateWinProbability(student, award, studentScores, studentAwardRecords);

                String recommendation = generateRecommendation(matchScore, winProbability, award);

                Map<String, Object> recommendationItem = new HashMap<>();
                recommendationItem.put("awardId", award.getId());
                recommendationItem.put("awardName", award.getAwardName());
                recommendationItem.put("awardLevel", award.getAwardLevel());
                recommendationItem.put("awardType", award.getAwardType());
                recommendationItem.put("matchScore", Math.round(matchScore * 100) / 100.0);
                recommendationItem.put("winProbability", Math.round(winProbability * 100) / 100.0);
                recommendationItem.put("recommendation", recommendation);
                recommendationItem.put("competitionLevel", getCompetitionLevel(award));

                recommendations.add(recommendationItem);
            }

            recommendations.sort((a, b) -> 
                Double.compare((Double) b.get("matchScore"), (Double) a.get("matchScore"))
            );

            return recommendations.stream().limit(10).collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("推荐奖项失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private double calculateMatchScore(Student student, Award award, 
                                      List<Score> studentScores, 
                                      List<StudentAwardRecord> studentAwardRecords) {
        
        RecommendationWeight weights = recommendationWeightService.getWeights();
        
        double gradeWeight = weights.getGradeWeight().divide(new BigDecimal("100")).doubleValue();
        double awardWeight = weights.getAwardWeight().divide(new BigDecimal("100")).doubleValue();
        double majorWeight = weights.getMajorWeight().divide(new BigDecimal("100")).doubleValue();
        double historyWeight = weights.getHistoryWeight().divide(new BigDecimal("100")).doubleValue();
        double competitionWeight = weights.getCompetitionWeight().divide(new BigDecimal("100")).doubleValue();
        
        double gradeScore = calculateGradeScore(studentScores) * gradeWeight;
        
        double awardScore = calculateAwardScore(award, studentAwardRecords) * awardWeight;
        
        double majorScore = calculateMajorScore(student, award) * majorWeight;
        
        double historyScore = calculateHistoryScore(award) * historyWeight;
        
        double competitionScore = calculateCompetitionScore(award) * competitionWeight;

        return gradeScore + awardScore + majorScore + historyScore + competitionScore;
    }

    private double calculateGradeScore(List<Score> studentScores) {
        if (studentScores == null || studentScores.isEmpty()) {
            return 0.0;
        }

        double totalScore = 0.0;
        double totalCredits = 0.0;

        for (Score score : studentScores) {
            if (score.getTotalScore() != null) {
                totalScore += score.getTotalScore().doubleValue();
                totalCredits += 1.0;
            }
        }

        if (totalCredits == 0) {
            return 0.0;
        }

        double avgScore = totalScore / totalCredits;

        if (avgScore >= 90) return 100.0;
        if (avgScore >= 85) return 90.0;
        if (avgScore >= 80) return 80.0;
        if (avgScore >= 75) return 70.0;
        if (avgScore >= 70) return 60.0;
        return 50.0;
    }

    private double calculateAwardScore(Award award, List<StudentAwardRecord> studentAwardRecords) {
        if (studentAwardRecords == null || studentAwardRecords.isEmpty()) {
            return 30.0;
        }

        int nationalCount = 0;
        int provincialCount = 0;
        int schoolCount = 0;
        int departmentCount = 0;

        for (StudentAwardRecord record : studentAwardRecords) {
            switch (record.getAwardLevel()) {
                case "national":
                    nationalCount++;
                    break;
                case "provincial":
                    provincialCount++;
                    break;
                case "school":
                    schoolCount++;
                    break;
                case "department":
                    departmentCount++;
                    break;
            }
        }

        double baseScore = 30.0;

        switch (award.getAwardLevel()) {
            case "national":
                if (nationalCount > 0) baseScore = 100.0;
                else if (provincialCount > 0) baseScore = 80.0;
                else if (schoolCount > 0) baseScore = 60.0;
                else baseScore = 40.0;
                break;
            case "provincial":
                if (provincialCount > 0 || nationalCount > 0) baseScore = 100.0;
                else if (schoolCount > 0) baseScore = 70.0;
                else baseScore = 50.0;
                break;
            case "school":
                if (studentAwardRecords.size() > 0) baseScore = 90.0;
                else baseScore = 60.0;
                break;
            case "department":
                baseScore = 80.0;
                break;
        }

        return baseScore;
    }

    private double calculateMajorScore(Student student, Award award) {
        String requirement = award.getRequirement();
        if (requirement == null) {
            return 100.0;
        }

        if (requirement.contains(student.getMajor())) {
            return 100.0;
        }

        return 70.0;
    }

    private double calculateHistoryScore(Award award) {
        Long applicationCount = studentAwardApplicationMapper.selectCount(
            new LambdaQueryWrapper<StudentAwardApplication>()
                .eq(StudentAwardApplication::getAwardId, award.getId())
        );

        if (applicationCount == 0) return 50.0;
        if (applicationCount < 10) return 70.0;
        if (applicationCount < 30) return 85.0;
        return 100.0;
    }

    private double calculateCompetitionScore(Award award) {
        Long applicationCount = studentAwardApplicationMapper.selectCount(
            new LambdaQueryWrapper<StudentAwardApplication>()
                .eq(StudentAwardApplication::getAwardId, award.getId())
        );

        Integer quota = award.getQuota();
        if (quota == null || quota == 0) {
            return 100.0;
        }

        double ratio = applicationCount.doubleValue() / quota;

        if (ratio < 1.0) return 100.0;
        if (ratio < 2.0) return 80.0;
        if (ratio < 3.0) return 60.0;
        if (ratio < 5.0) return 40.0;
        return 20.0;
    }

    private double calculateWinProbability(Student student, Award award, 
                                          List<Score> studentScores, 
                                          List<StudentAwardRecord> studentAwardRecords) {
        
        double matchScore = calculateMatchScore(student, award, studentScores, studentAwardRecords);
        
        Long totalApplications = studentAwardApplicationMapper.selectCount(
            new LambdaQueryWrapper<StudentAwardApplication>()
                .eq(StudentAwardApplication::getAwardId, award.getId())
        );

        Integer quota = award.getQuota();
        if (quota == null || quota == 0) {
            quota = 10;
        }

        double baseProb = matchScore / 100.0;

        double competitionFactor = 1.0;
        if (totalApplications > 0) {
            double ratio = totalApplications.doubleValue() / quota;
            competitionFactor = Math.max(0.3, 1.0 - (ratio - 1.0) * 0.15);
        }

        double probability = baseProb * competitionFactor * 100;

        return Math.min(95.0, Math.max(5.0, probability));
    }

    private String generateRecommendation(double matchScore, double winProbability, Award award) {
        StringBuilder sb = new StringBuilder();

        if (matchScore >= 85 && winProbability >= 70) {
            sb.append("🌟 强烈推荐！您的条件非常符合该奖项要求，获奖概率很高。");
        } else if (matchScore >= 70 && winProbability >= 50) {
            sb.append("✅ 推荐申请！您具备较好的竞争力，建议准备充分的申请材料。");
        } else if (matchScore >= 60) {
            sb.append("💡 可以尝试！您的条件基本符合，但竞争可能较激烈，建议突出个人优势。");
        } else {
            sb.append("⚠️ 谨慎考虑！该奖项要求较高，建议先提升自身条件或选择其他奖项。");
        }

        if (award.getRequirement() != null && !award.getRequirement().isEmpty()) {
            sb.append("\n📋 评奖要求：").append(award.getRequirement());
        }

        return sb.toString();
    }

    private String getCompetitionLevel(Award award) {
        Long applicationCount = studentAwardApplicationMapper.selectCount(
            new LambdaQueryWrapper<StudentAwardApplication>()
                .eq(StudentAwardApplication::getAwardId, award.getId())
        );

        Integer quota = award.getQuota();
        if (quota == null || quota == 0) {
            return "未知";
        }

        double ratio = applicationCount.doubleValue() / quota;

        if (ratio < 1.5) return "竞争较小";
        if (ratio < 3.0) return "竞争适中";
        if (ratio < 5.0) return "竞争激烈";
        return "竞争非常激烈";
    }
}