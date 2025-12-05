package com.example.studentselectionsystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentselectionsystem.entity.Standard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

/**
 * 评奖标准数据访问接口
 */
@Mapper
public interface StandardRepository extends BaseMapper<Standard> {

    /**
     * 根据标准名称查找标准
     * @param name 标准名称
     * @return 标准信息
     */
    @Select("SELECT * FROM standards WHERE name = #{name}")
    Optional<Standard> selectByName(@Param("name") String name);

    /**
     * 根据标准代码查找标准
     * @param code 标准代码
     * @return 标准信息
     */
    @Select("SELECT * FROM standards WHERE code = #{code}")
    Optional<Standard> selectByCode(@Param("code") String code);

    /**
     * 判断标准名称是否存在
     * @param name 标准名称
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM standards WHERE name = #{name}")
    boolean existsByName(@Param("name") String name);

    /**
     * 判断标准代码是否存在
     * @param code 标准代码
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM standards WHERE code = #{code}")
    boolean existsByCode(@Param("code") String code);

}