package com.example.studentselectionsystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentselectionsystem.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RoleRepository extends BaseMapper<Role> {

    @Select("SELECT * FROM role WHERE name = #{name}")
    Optional<Role> selectByName(@Param("name") String name);

    @Select("SELECT r.*, p.* FROM role r LEFT JOIN role_permission rp ON r.id = rp.role_id LEFT JOIN permission p ON rp.permission_id = p.id WHERE r.id = #{id}")
    Optional<Role> selectByIdWithPermissions(@Param("id") Integer id);

    @Select("SELECT r.*, p.* FROM role r LEFT JOIN role_permission rp ON r.id = rp.role_id LEFT JOIN permission p ON rp.permission_id = p.id")
    List<Role> selectAllWithPermissions();

    @Select("SELECT COUNT(*) > 0 FROM role WHERE name = #{name}")
    boolean existsByName(@Param("name") String name);
}