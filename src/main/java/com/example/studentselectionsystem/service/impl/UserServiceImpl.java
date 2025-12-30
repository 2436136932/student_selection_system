package com.example.studentselectionsystem.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentselectionsystem.entity.Role;
import com.example.studentselectionsystem.entity.User;
import com.example.studentselectionsystem.repository.RoleRepository;
import com.example.studentselectionsystem.repository.UserRepository;
import com.example.studentselectionsystem.service.UserService;
import com.example.studentselectionsystem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户服务实现类
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StudentService studentService;

    @Value("${file.storage.dir}")
    private String storageDir;

    @Value("${file.storage.allowed-extensions}")
    private String allowedExtensions;

    @Override
    public User createUser(User user) {
        // 设置创建时间
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        // 设置默认状态为1（正常）
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        userRepository.insert(user);
        return user;
    }

    @Override
    public User updateUser(Long id, User user) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.selectById(id));
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            // 更新用户信息
            
            // 只有当前端明确发送了realName字段时才更新
            if (user.getRealName() != null) {
                existingUser.setRealName(user.getRealName());
            }
            
            // 只有当前端明确发送了email字段时才更新
            if (user.getEmail() != null) {
                existingUser.setEmail(user.getEmail());
            }
            
            // 只有当前端明确发送了phone字段时才更新
            if (user.getPhone() != null) {
                existingUser.setPhone(user.getPhone());
            }
            
            // 只有当前端明确发送了status字段时才更新（避免意外修改状态）
            if (user.getStatus() != null) {
                existingUser.setStatus(user.getStatus());
            }
            
            existingUser.setUpdateTime(new Date());
            
            // 如果更新了密码，则重新加密
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            
            userRepository.updateById(existingUser);
            return existingUser;
        }
        return null;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(userRepository.selectById(id));
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        logger.info("正在查询用户名: {}", username);
        Optional<User> userOptional = userRepository.selectByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            logger.info("查询结果: 用户存在，ID={}, 用户名={}, 密码={}, 角色={}, 状态={}", 
                    user.getId(), 
                    user.getUsername(), 
                    user.getPassword(),
                    user.getRole(),
                    user.getStatus());
        } else {
            logger.info("查询结果: 用户 '{}' 不存在", username);
        }
        return userOptional;
    }
    
    @Override
    public Optional<User> findUserByRealName(String realName) {
        logger.info("正在查询真实姓名: {}", realName);
        Optional<User> userOptional = userRepository.selectByRealName(realName);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            logger.info("查询结果: 用户存在，ID={}, 用户名={}, 真实姓名={}, 邮箱={}, 电话={}", 
                    user.getId(), 
                    user.getUsername(), 
                    user.getRealName(),
                    user.getEmail(),
                    user.getPhone());
        } else {
            logger.info("查询结果: 真实姓名为 '{}' 的用户不存在", realName);
        }
        return userOptional;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.selectByEmail(email);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.selectList(null);
    }

    @Override
    public IPage<User> findUsersByPage(IPage<User> page) {
        return userRepository.selectPage(page, null);
    }

    @Override
    public IPage<User> findUsersByPage(Integer current, Integer size) {
        // 创建MyBatis Plus分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        return userRepository.selectPage(page, null);
    }

    @Override
    public User assignRolesToUser(Long userId, List<Long> roleIds) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.selectById(userId));
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // 获取角色列表
            List<Role> roles = roleRepository.selectBatchIds(roleIds);
            user.setRoles(roles);
            userRepository.updateById(user);
            return user;
        }
        return null;
    }

    @Override
    public Optional<User> findUserByIdWithRoles(Long id) {
        return userRepository.selectByIdWithRoles(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User updateAvatar(MultipartFile file) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> optionalUser = findUserByUsername(username);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            try {
                // 确保存储目录存在
                File dir = new File(storageDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                
                // 生成唯一文件名
                String originalFilename = file.getOriginalFilename();
                String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
                String fileName = UUID.randomUUID().toString() + fileExtension;
                
                // 保存文件
                File dest = new File(storageDir + File.separator + fileName);
                file.transferTo(dest);
                
                // 更新用户头像URL，使用相对路径或完整URL
                String avatarUrl = "/uploads/" + fileName;
                user.setAvatar(avatarUrl);
                user.setUpdateTime(new Date());
                
                // 保存更新后的用户信息
                userRepository.updateById(user);
                
                return user;
            } catch (IOException e) {
                logger.error("Failed to upload avatar: {}", e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean changePassword(String oldPassword, String newPassword) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> optionalUser = findUserByUsername(username);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // 验证旧密码是否正确
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                // 加密新密码
                String encodedNewPassword = passwordEncoder.encode(newPassword);
                
                // 更新用户密码
                user.setPassword(encodedNewPassword);
                user.setUpdateTime(new Date());
                userRepository.updateById(user);
                
                return true;
            }
        }
        return false;
    }

    @Override
    public User findCurrentUser() {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> optionalUser = findUserByUsername(username);
        
        return optionalUser.orElse(null);
    }

    @Override
    public boolean resetPasswordByEmail(String email, String newPassword) {
        // 根据邮箱查找用户
        Optional<User> optionalUser = findUserByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // 加密新密码
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            // 更新用户密码
            user.setPassword(encodedNewPassword);
            user.setUpdateTime(new Date());
            userRepository.updateById(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean isCurrentUserStudent(Long studentId) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // 检查是否为学生角色
            boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT") || a.getAuthority().equals("STUDENT"));
            if (isStudent) {
                try {
                    String username = authentication.getName();
                    // 通过用户名查询用户信息
                    Optional<User> userOptional = findUserByUsername(username);
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        // 通过用户ID查询学生信息
                        Optional<com.example.studentselectionsystem.entity.Student> studentOptional = 
                            studentService.findStudentByUserId(user.getId());
                        if (studentOptional.isPresent()) {
                            return studentOptional.get().getId().equals(studentId);
                        }
                    }
                } catch (Exception e) {
                    logger.error("检查当前用户是否为指定学生失败: {}", e.getMessage());
                }
            }
        }
        return false;
    }

}