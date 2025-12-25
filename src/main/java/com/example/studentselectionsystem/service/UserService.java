package com.example.studentselectionsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 创建用户
     * @param user 用户信息
     * @return 创建的用户
     */
    User createUser(User user);

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新后的用户
     */
    User updateUser(Long id, User user);

    /**
     * 删除用户
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户信息
     */
    Optional<User> findUserById(Long id);

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> findUserByUsername(String username);

    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户信息
     */
    Optional<User> findUserByEmail(String email);

    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<User> findAllUsers();

    /**
     * 分页获取用户
     * @param page 分页参数
     * @return 用户分页列表
     */
    IPage<User> findUsersByPage(IPage<User> page);

    /**
     * 分页获取用户
     * @param current 页码（从1开始）
     * @param size 每页大小
     * @return 用户分页列表
     */
    IPage<User> findUsersByPage(Integer current, Integer size);
    
    /**
     * 检查当前登录用户是否是指定ID的学生
     * @param studentId 学生ID
     * @return 是否是当前登录学生
     */
    boolean isCurrentUserStudent(Long studentId);

    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 更新后的用户
     */
    User assignRolesToUser(Long userId, List<Long> roleIds);

    /**
     * 根据用户ID获取用户及其角色信息
     * @param id 用户ID
     * @return 用户信息
     */
    Optional<User> findUserByIdWithRoles(Long id);

    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 根据真实姓名查找用户
     * @param realName 真实姓名
     * @return 用户信息
     */
    Optional<User> findUserByRealName(String realName);
    
    /**
     * 检查邮箱是否已存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 更新用户头像
     * @param file 头像文件
     * @return 更新后的用户信息
     */
    User updateAvatar(org.springframework.web.multipart.MultipartFile file);

    /**
     * 修改用户密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    boolean changePassword(String oldPassword, String newPassword);

    /**
     * 获取当前登录用户信息
     * @return 当前登录用户信息
     */
    User findCurrentUser();
    
    /**
     * 通过邮箱重置密码
     * @param email 邮箱
     * @param newPassword 新密码
     * @return 是否重置成功
     */
    boolean resetPasswordByEmail(String email, String newPassword);
}
