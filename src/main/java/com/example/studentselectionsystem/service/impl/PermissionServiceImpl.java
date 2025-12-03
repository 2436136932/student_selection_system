package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Permission;
import com.example.studentselectionsystem.repository.PermissionRepository;
import com.example.studentselectionsystem.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 权限服务实现类
 */
@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public Permission createPermission(Permission permission) {
        // 设置创建时间
        permission.setCreateTime(new Date());
        permissionRepository.insert(permission);
        return permission;
    }

    @Override
    public Permission updatePermission(Integer id, Permission permission) {
        Permission existingPermission = permissionRepository.selectById(id);
        if (existingPermission != null) {
            // 更新权限信息
            existingPermission.setName(permission.getName());
            existingPermission.setDescription(permission.getDescription());
            permissionRepository.updateById(existingPermission);
            return existingPermission;
        }
        return null;
    }

    @Override
    public void deletePermission(Integer id) {
        permissionRepository.deleteById(id);
    }

    @Override
    public Optional<Permission> findPermissionById(Integer id) {
        return Optional.ofNullable(permissionRepository.selectById(id));
    }

    @Override
    public Optional<Permission> findPermissionByName(String name) {
        return Optional.ofNullable(permissionRepository.selectByName(name));
    }

    @Override
    public List<Permission> findAllPermissions() {
        return permissionRepository.selectList(null);
    }

    @Override
    public IPage<Permission> findPermissionsByPage(IPage<Permission> page) {
        return permissionRepository.selectPage(page, null);
    }

    @Override
    public IPage<Permission> findPermissionsByPage(Integer current, Integer size) {
        // 创建MyBatis Plus分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Permission> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        return permissionRepository.selectPage(page, null);
    }

    @Override
    public boolean existsByName(String name) {
        return permissionRepository.existsByName(name);
    }

}