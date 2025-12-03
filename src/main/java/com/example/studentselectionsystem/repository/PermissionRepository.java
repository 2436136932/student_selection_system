package com.example.studentselectionsystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentselectionsystem.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 权限数据访问接口
 */
@Mapper
public interface PermissionRepository extends BaseMapper<Permission> {

    /**
     * 根据权限名称查找权限
     * @param name 权限名称
     * @return 权限信息
     */
    @Select("SELECT * FROM permission WHERE name = #{name}")
    Permission selectByName(@Param("name") String name);

    /**
     * 判断权限名称是否存在
     * @param name 权限名称
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM permission WHERE name = #{name}")
    boolean existsByName(@Param("name") String name);

}