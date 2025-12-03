package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Permission;

import java.util.List;
import java.util.Optional;

/**
 * 权限服务接口
 */
public interface PermissionService {

    /**
     * 创建权限
     * @param permission 权限信息
     * @return 创建的权限
     */
    Permission createPermission(Permission permission);

    /**
     * 更新权限信息
     * @param id 权限ID
     * @param permission 权限信息
     * @return 更新后的权限
     */
    Permission updatePermission(Integer id, Permission permission);

    /**
     * 删除权限
     * @param id 权限ID
     */
    void deletePermission(Integer id);

    /**
     * 根据ID查找权限
     * @param id 权限ID
     * @return 权限信息
     */
    Optional<Permission> findPermissionById(Integer id);

    /**
     * 根据权限名称查找权限
     * @param name 权限名称
     * @return 权限信息
     */
    Optional<Permission> findPermissionByName(String name);

    /**
     * 获取所有权限
     * @return 权限列表
     */
    List<Permission> findAllPermissions();

    /**
     * 分页获取权限
     * @param page 分页参数
     * @return 权限分页列表
     */
    IPage<Permission> findPermissionsByPage(IPage<Permission> page);

    /**
     * 分页获取权限
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @return 权限分页列表
     */
    IPage<Permission> findPermissionsByPage(Integer current, Integer size);

    /**
     * 判断权限名称是否存在
     * @param name 权限名称
     * @return 是否存在
     */
    boolean existsByName(String name);

}