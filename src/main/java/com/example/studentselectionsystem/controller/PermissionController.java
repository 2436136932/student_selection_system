package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Permission;
import com.example.studentselectionsystem.service.PermissionService;
import com.example.studentselectionsystem.util.PageResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 权限控制器
 */
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 创建权限
     */
    @PostMapping
    public ResponseEntity<Permission> createPermission(@RequestBody Permission permission) {
        Permission createdPermission = permissionService.createPermission(permission);
        return new ResponseEntity<>(createdPermission, HttpStatus.CREATED);
    }

    /**
     * 更新权限信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Permission> updatePermission(@PathVariable Integer id, @RequestBody Permission permission) {
        Permission updatedPermission = permissionService.updatePermission(id, permission);
        if (updatedPermission != null) {
            return new ResponseEntity<>(updatedPermission, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Integer id) {
        permissionService.deletePermission(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 根据ID查找权限
     */
    @GetMapping("/{id}")
    public ResponseEntity<Permission> findPermissionById(@PathVariable Integer id) {
        Optional<Permission> optionalPermission = permissionService.findPermissionById(id);
        return optionalPermission.map(permission -> new ResponseEntity<>(permission, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 根据权限名称查找权限
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Permission> findPermissionByName(@PathVariable String name) {
        Optional<Permission> optionalPermission = permissionService.findPermissionByName(name);
        return optionalPermission.map(permission -> new ResponseEntity<>(permission, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 获取所有权限
     */
    @GetMapping
    public ResponseEntity<List<Permission>> findAllPermissions() {
        List<Permission> permissions = permissionService.findAllPermissions();
        return new ResponseEntity<>(permissions, HttpStatus.OK);
    }

    /**
     * 分页获取权限
     * @param current 页码（从1开始）
     * @param size 每页大小
     */
    @GetMapping("/page")
    public ResponseEntity<?> findPermissionsByPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        IPage<Permission> permissions = permissionService.findPermissionsByPage(current, size);
        return ResponseEntity.ok(PageResultUtil.convertToPageResult(permissions));
    }

    /**
     * 检查权限名称是否已存在
     */
    @GetMapping("/check-name/{name}")
    public ResponseEntity<Boolean> checkNameExists(@PathVariable String name) {
        boolean exists = permissionService.existsByName(name);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

}