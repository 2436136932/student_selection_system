package com.example.studentselectionsystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentselectionsystem.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

/**
 * 课程数据访问接口
 */
@Mapper
public interface CourseRepository extends BaseMapper<Course> {

    /**
     * 根据课程名称查找课程
     * @param name 课程名称
     * @return 课程信息
     */
    @Select("SELECT * FROM course WHERE name = #{name}")
    Optional<Course> selectByName(@Param("name") String name);

    /**
     * 根据课程代码查找课程
     * @param code 课程代码
     * @return 课程信息
     */
    @Select("SELECT * FROM course WHERE code = #{code}")
    Optional<Course> selectByCode(@Param("code") String code);

    /**
     * 判断课程名称是否存在
     * @param name 课程名称
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM course WHERE name = #{name}")
    boolean existsByName(@Param("name") String name);

    /**
     * 判断课程代码是否存在
     * @param code 课程代码
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM course WHERE code = #{code}")
    boolean existsByCode(@Param("code") String code);

}