package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Permission;
import com.example.studentselectionsystem.entity.Role;
import com.example.studentselectionsystem.repository.PermissionRepository;
import com.example.studentselectionsystem.repository.RoleRepository;
import com.example.studentselectionsystem.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 角色服务实现类
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public Role createRole(Role role) {
        // 设置创建时间
        role.setCreateTime(new Date());
        roleRepository.insert(role);
        return role;
    }

    @Override
    public Role updateRole(Long id, Role role) {
        Role existingRole = roleRepository.selectById(id);
        if (existingRole != null) {
            // 更新角色信息
            existingRole.setName(role.getName());
            existingRole.setDescription(role.getDescription());
            roleRepository.updateById(existingRole);
            return existingRole;
        }
        return null;
    }

    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public Optional<Role> findRoleById(Long id) {
        return Optional.ofNullable(roleRepository.selectById(id));
    }

    @Override
    public Optional<Role> findRoleByName(String name) {
        return roleRepository.selectByName(name);
    }

    @Override
    public List<Role> findAllRoles() {
        return roleRepository.selectList(null);
    }

    @Override
    public IPage<Role> findRolesByPage(IPage<Role> page) {
        return roleRepository.selectPage(page, null);
    }

    @Override
    public IPage<Role> findRolesByPage(Integer current, Integer size) {
        // 创建MyBatis Plus分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Role> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        return roleRepository.selectPage(page, null);
    }

    @Override
    public Role assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.selectById(roleId);
        if (role != null) {
            // 获取权限列表
            List<Permission> permissions = permissionRepository.selectBatchIds(permissionIds);
            role.setPermissions(permissions);
            roleRepository.updateById(role);
            return role;
        }
        return null;
    }

    @Override
    public Optional<Role> findRoleByIdWithPermissions(Long id) {
        return roleRepository.selectByIdWithPermissions(id);
    }

    @Override
    public List<Role> findAllRolesWithPermissions() {
        return roleRepository.selectAllWithPermissions();
    }

    @Override
    public boolean existsByName(String name) {
        return roleRepository.selectByName(name) != null;
    }

}