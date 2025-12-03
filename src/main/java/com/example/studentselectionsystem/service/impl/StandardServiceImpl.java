package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Course;
import com.example.studentselectionsystem.entity.Standard;
import com.example.studentselectionsystem.repository.CourseRepository;
import com.example.studentselectionsystem.service.StandardService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 评奖标准服务实现类
 */
@Service
public class StandardServiceImpl implements StandardService {

    @Autowired
    private CourseRepository courseRepository;

    /**
     * 创建评奖标准
     * @param standard 评奖标准信息
     * @return 创建的评奖标准
     */
    @Override
    public Standard createStandard(Standard standard) {
        // 转换为Course实体进行保存
        Course course = new Course();
        BeanUtils.copyProperties(standard, course, "weight");
        course.setCredit(standard.getWeight()); // 权重映射到学分字段
        
        courseRepository.insert(course);
        
        // 转换回Standard实体返回
        BeanUtils.copyProperties(course, standard, "credit");
        standard.setWeight(course.getCredit());
        return standard;
    }

    /**
     * 更新评奖标准信息
     * @param id 标准ID
     * @param standard 标准信息
     * @return 更新后的标准
     */
    @Override
    public Standard updateStandard(Integer id, Standard standard) {
        Optional<Course> existingCourse = Optional.ofNullable(courseRepository.selectById(id));
        if (existingCourse.isPresent()) {
            Course updatedCourse = existingCourse.get();
            updatedCourse.setName(standard.getName());
            updatedCourse.setCredit(standard.getWeight()); // 权重映射到学分字段
            updatedCourse.setSemester(standard.getSemester());
            courseRepository.updateById(updatedCourse);
            
            // 转换回Standard实体返回
            BeanUtils.copyProperties(updatedCourse, standard, "credit");
            standard.setWeight(updatedCourse.getCredit());
            return standard;
        }
        return null;
    }

    /**
     * 删除评奖标准
     * @param id 标准ID
     */
    @Override
    public void deleteStandard(Integer id) {
        courseRepository.deleteById(id);
    }

    /**
     * 根据ID查找标准
     * @param id 标准ID
     * @return 标准信息
     */
    @Override
    public Optional<Standard> findStandardById(Integer id) {
        Optional<Course> course = Optional.ofNullable(courseRepository.selectById(id));
        return course.map(this::convertToStandard);
    }

    /**
     * 根据标准名称查找标准
     * @param name 标准名称
     * @return 标准信息
     */
    @Override
    public Optional<Standard> findStandardByName(String name) {
        Optional<Course> course = courseRepository.selectByName(name);
        return course.map(this::convertToStandard);
    }

    /**
     * 根据标准代码查找标准
     * @param code 标准代码
     * @return 标准信息
     */
    @Override
    public Optional<Standard> findStandardByCode(String code) {
        Optional<Course> course = courseRepository.selectByCode(code);
        return course.map(this::convertToStandard);
    }

    /**
     * 获取所有标准
     * @return 标准列表
     */
    @Override
    public List<Standard> findAllStandards() {
        List<Course> courses = courseRepository.selectList(null);
        return courses.stream()
                .map(this::convertToStandard)
                .collect(Collectors.toList());
    }

    /**
     * 分页获取标准
     * @param page 分页参数
     * @return 标准分页列表
     */
    @Override
    public IPage<Standard> findStandardsByPage(IPage<Standard> page) {
        // 创建Course的分页对象
        IPage<Course> coursePage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getCurrent(), page.getSize());
        coursePage = courseRepository.selectPage(coursePage, null);
        
        // 转换为Standard的分页对象
        IPage<Standard> standardPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getCurrent(), page.getSize());
        standardPage.setTotal(coursePage.getTotal());
        standardPage.setPages(coursePage.getPages());
        standardPage.setRecords(coursePage.getRecords().stream()
                .map(this::convertToStandard)
                .collect(Collectors.toList()));
        
        return standardPage;
    }

    /**
     * 分页获取标准
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @return 标准分页列表
     */
    @Override
    public IPage<Standard> findStandardsByPage(Integer current, Integer size) {
        // 创建Course的分页对象
        IPage<Course> coursePage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        coursePage = courseRepository.selectPage(coursePage, null);
        
        // 转换为Standard的分页对象
        IPage<Standard> standardPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        standardPage.setTotal(coursePage.getTotal());
        standardPage.setPages(coursePage.getPages());
        standardPage.setRecords(coursePage.getRecords().stream()
                .map(this::convertToStandard)
                .collect(Collectors.toList()));
        
        return standardPage;
    }

    /**
     * 判断标准名称是否存在
     * @param name 标准名称
     * @return 是否存在
     */
    @Override
    public boolean existsByName(String name) {
        return courseRepository.existsByName(name);
    }

    /**
     * 判断标准代码是否存在
     * @param code 标准代码
     * @return 是否存在
     */
    @Override
    public boolean existsByCode(String code) {
        return courseRepository.existsByCode(code);
    }
    
    /**
     * 将Course实体转换为Standard实体
     * @param course Course实体
     * @return Standard实体
     */
    private Standard convertToStandard(Course course) {
        Standard standard = new Standard();
        BeanUtils.copyProperties(course, standard, "credit");
        standard.setWeight(course.getCredit()); // 学分字段映射到权重
        return standard;
    }
}