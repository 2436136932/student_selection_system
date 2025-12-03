package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Role;

import java.util.List;
import java.util.Optional;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 创建角色
     * @param role 角色信息
     * @return 创建的角色
     */
    Role createRole(Role role);

    /**
     * 更新角色信息
     * @param id 角色ID
     * @param role 角色信息
     * @return 更新后的角色
     */
    Role updateRole(Integer id, Role role);

    /**
     * 删除角色
     * @param id 角色ID
     */
    void deleteRole(Integer id);

    /**
     * 根据ID查找角色
     * @param id 角色ID
     * @return 角色信息
     */
    Optional<Role> findRoleById(Integer id);

    /**
     * 根据角色名称查找角色
     * @param name 角色名称
     * @return 角色信息
     */
    Optional<Role> findRoleByName(String name);

    /**
     * 获取所有角色
     * @return 角色列表
     */
    List<Role> findAllRoles();

    /**
     * 分页获取角色
     * @param page 分页参数
     * @return 角色分页列表
     */
    IPage<Role> findRolesByPage(IPage<Role> page);

    /**
     * 分页获取角色
     * @param current 当前页
     * @param size 每页大小
     * @return 角色分页列表
     */
    IPage<Role> findRolesByPage(Integer current, Integer size);

    /**
     * 为角色分配权限
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 更新后的角色
     */
    Role assignPermissionsToRole(Integer roleId, List<Integer> permissionIds);

    /**
     * 根据角色ID获取角色及其权限信息
     * @param id 角色ID
     * @return 角色信息
     */
    Optional<Role> findRoleByIdWithPermissions(Integer id);

    /**
     * 获取所有角色及其权限信息
     * @return 角色列表
     */
    List<Role> findAllRolesWithPermissions();

    /**
     * 判断角色名称是否存在
     * @param name 角色名称
     * @return 是否存在
     */
    boolean existsByName(String name);

}