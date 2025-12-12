package com.example.studentselectionsystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentselectionsystem.entity.Major;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * 专业信息数据访问接口
 */
@Mapper
public interface MajorRepository extends BaseMapper<Major> {

    /**
     * 根据专业名称查找专业
     * @param name 专业名称
     * @return 专业信息
     */
    @Select("SELECT * FROM majors WHERE name = #{name}")
    Optional<Major> selectByName(@Param("name") String name);

    /**
     * 根据学院查找专业
     * @param department 学院
     * @return 专业列表
     */
    @Select("SELECT * FROM majors WHERE department = #{department}")
    List<Major> selectByDepartment(@Param("department") String department);

    /**
     * 判断专业名称是否存在
     * @param name 专业名称
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM majors WHERE name = #{name}")
    boolean existsByName(@Param("name") String name);
}