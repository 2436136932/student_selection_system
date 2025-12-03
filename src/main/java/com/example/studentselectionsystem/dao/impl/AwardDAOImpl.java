package com.example.studentselectionsystem.dao.impl;

import com.example.studentselectionsystem.dao.AwardDAO;
import com.example.studentselectionsystem.model.Award;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 评奖评优DAO实现类
 */
@Repository
public class AwardDAOImpl implements AwardDAO {
    // 使用Map存储奖项信息，键为奖项ID
    private final Map<String, Award> awardMap = new ConcurrentHashMap<>();

    @Override
    public boolean addAward(Award award) {
        if (awardMap.containsKey(award.getAwardId())) {
            return false;
        }
        awardMap.put(award.getAwardId(), award);
        return true;
    }

    @Override
    public boolean updateAward(Award award) {
        if (!awardMap.containsKey(award.getAwardId())) {
            return false;
        }
        awardMap.put(award.getAwardId(), award);
        return true;
    }

    @Override
    public boolean deleteAward(String awardId) {
        return awardMap.remove(awardId) != null;
    }

    @Override
    public Optional<Award> getAwardById(String awardId) {
        return Optional.ofNullable(awardMap.get(awardId));
    }

    @Override
    public List<Award> getAwardsByStudentId(String studentId) {
        List<Award> result = new ArrayList<>();
        for (Award award : awardMap.values()) {
            if (award.getStudentId().equals(studentId)) {
                result.add(award);
            }
        }
        return result;
    }

    @Override
    public List<Award> getAllAwards() {
        return new ArrayList<>(awardMap.values());
    }

    @Override
    public List<Award> getAwardsByPage(int page, int size, String studentId, String awardName, String year) {
        List<Award> filteredList = new ArrayList<>();
        
        // 筛选奖项
        for (Award award : awardMap.values()) {
            boolean match = true;
            
            if (studentId != null && !studentId.isEmpty() && !award.getStudentId().equals(studentId)) {
                match = false;
            }
            
            if (awardName != null && !awardName.isEmpty() && !award.getAwardName().contains(awardName)) {
                match = false;
            }
            
            if (year != null && !year.isEmpty() && !award.getYear().equals(year)) {
                match = false;
            }
            
            if (match) {
                filteredList.add(award);
            }
        }
        
        // 分页处理
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, filteredList.size());
        
        if (startIndex >= filteredList.size()) {
            return new ArrayList<>();
        }
        
        return filteredList.subList(startIndex, endIndex);
    }

    @Override
    public long getTotalAwards(String studentId, String awardName, String year) {
        long count = 0;
        
        for (Award award : awardMap.values()) {
            boolean match = true;
            
            if (studentId != null && !studentId.isEmpty() && !award.getStudentId().equals(studentId)) {
                match = false;
            }
            
            if (awardName != null && !awardName.isEmpty() && !award.getAwardName().contains(awardName)) {
                match = false;
            }
            
            if (year != null && !year.isEmpty() && !award.getYear().equals(year)) {
                match = false;
            }
            
            if (match) {
                count++;
            }
        }
        
        return count;
    }
}