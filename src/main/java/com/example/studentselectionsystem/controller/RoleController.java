package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Role;
import com.example.studentselectionsystem.service.RoleService;
import com.example.studentselectionsystem.util.PageResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 角色控制器
 */
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 创建角色
     */
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role createdRole = roleService.createRole(role);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }

    /**
     * 更新角色信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        Role updatedRole = roleService.updateRole(id, role);
        if (updatedRole != null) {
            return new ResponseEntity<>(updatedRole, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 根据ID查找角色
     */
    @GetMapping("/{id}")
    public ResponseEntity<Role> findRoleById(@PathVariable Long id) {
        Optional<Role> optionalRole = roleService.findRoleById(id);
        return optionalRole.map(role -> new ResponseEntity<>(role, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 根据ID查找角色及其权限信息
     */
    @GetMapping("/{id}/with-permissions")
    public ResponseEntity<Role> findRoleByIdWithPermissions(@PathVariable Long id) {
        Optional<Role> optionalRole = roleService.findRoleByIdWithPermissions(id);
        return optionalRole.map(role -> new ResponseEntity<>(role, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 根据角色名称查找角色
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Role> findRoleByName(@PathVariable String name) {
        Optional<Role> optionalRole = roleService.findRoleByName(name);
        return optionalRole.map(role -> new ResponseEntity<>(role, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 获取所有角色
     */
    @GetMapping
    public ResponseEntity<List<Role>> findAllRoles() {
        List<Role> roles = roleService.findAllRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    /**
     * 获取所有角色及其权限信息
     */
    @GetMapping("/with-permissions")
    public ResponseEntity<List<Role>> findAllRolesWithPermissions() {
        List<Role> roles = roleService.findAllRolesWithPermissions();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    /**
     * 分页获取角色
     */
    @GetMapping("/page")
    public ResponseEntity<?> findRolesByPage(@RequestParam(defaultValue = "1") Integer current, 
                                                       @RequestParam(defaultValue = "10") Integer size) {
        IPage<Role> roles = roleService.findRolesByPage(current, size);
        return ResponseEntity.ok(PageResultUtil.convertToPageResult(roles));
    }

    /**
     * 为角色分配权限
     */
    @PostMapping("/{id}/permissions")
    public ResponseEntity<Role> assignPermissionsToRole(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        Role updatedRole = roleService.assignPermissionsToRole(id, permissionIds);
        if (updatedRole != null) {
            return new ResponseEntity<>(updatedRole, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * 检查角色名称是否已存在
     */
    @GetMapping("/check-name/{name}")
    public ResponseEntity<Boolean> checkNameExists(@PathVariable String name) {
        boolean exists = roleService.existsByName(name);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

}