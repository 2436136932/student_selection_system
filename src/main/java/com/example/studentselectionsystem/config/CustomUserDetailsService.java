package com.example.studentselectionsystem.config;

import com.example.studentselectionsystem.entity.User;
import com.example.studentselectionsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

/**
 * 自定义UserDetailsService实现，用于从数据库获取用户信息
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userService.findUserByUsername(username);
        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("用户 " + username + " 不存在");
        }

        User user = userOptional.get();
        
        // 创建Spring Security所需的UserDetails对象
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == 1, // 启用状态
                true, // 账户未过期
                true, // 密码未过期
                true, // 账户未锁定
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase())) // 角色信息转换为大写
        );
    }
}
