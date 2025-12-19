package com.example.studentselectionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentselectionsystem.entity.User;
import com.example.studentselectionsystem.service.UserService;
import com.example.studentselectionsystem.util.PageResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 创建用户
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 根据ID查找用户
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or (authentication.principal.username == @userService.findUserById(#id).get().getUsername())")
    public ResponseEntity<User> findUserById(@PathVariable Long id) {
        Optional<User> optionalUser = userService.findUserById(id);
        return optionalUser.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 根据ID查找用户及其角色信息
     */
    @GetMapping("/{id}/with-roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> findUserByIdWithRoles(@PathVariable Long id) {
        Optional<User> optionalUser = userService.findUserByIdWithRoles(id);
        return optionalUser.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 根据用户名查找用户
     */
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or authentication.principal.username == #username")
    public ResponseEntity<User> findUserByUsername(@PathVariable String username) {
        Optional<User> optionalUser = userService.findUserByUsername(username);
        return optionalUser.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/realname/{realName}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<User> findUserByRealName(@PathVariable String realName) {
        Optional<User> optionalUser = userService.findUserByRealName(realName);
        return optionalUser.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 获取所有用户
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<User>> findAllUsers() {
        List<User> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * 分页获取用户
     */
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<?> findUsersByPage(@RequestParam(defaultValue = "1") Integer current,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        IPage<User> users = userService.findUsersByPage(current, size);
        return ResponseEntity.ok(PageResultUtil.convertToPageResult(users));
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> assignRolesToUser(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        User updatedUser = userService.assignRolesToUser(id, roleIds);
        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * 检查用户名是否已存在
     */
    @GetMapping("/check-username/{username}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    /**
     * 检查邮箱是否已存在
     */
    @GetMapping("/check-email/{email}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    /**
     * 更新用户头像
     */
    @PostMapping("/avatar")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<User> updateAvatar(@RequestParam("file") MultipartFile file) {
        User updatedUser = userService.updateAvatar(file);
        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 修改用户密码
     */
    @PutMapping("/change-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest passwordChangeRequest) {
        boolean success = userService.changePassword(passwordChangeRequest.getOldPassword(), passwordChangeRequest.getNewPassword());
        if (success) {
            return ResponseEntity.ok("密码修改成功");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("旧密码错误");
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<User> getCurrentUser() {
        User currentUser = userService.findCurrentUser();
        if (currentUser != null) {
            return new ResponseEntity<>(currentUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * 密码修改请求DTO
     */
    public static class PasswordChangeRequest {
        private String oldPassword;
        private String newPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

}