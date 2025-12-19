package com.example.studentselectionsystem.controller;

import com.example.studentselectionsystem.entity.User;
import com.example.studentselectionsystem.service.UserService;
import com.example.studentselectionsystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.studentselectionsystem.entity.Student;
import com.example.studentselectionsystem.service.StudentService;
import java.util.Optional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 认证控制器 - 处理登录注册请求
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private StudentService studentService;

    /**
     * 用户登录
     * @param loginRequest 登录请求参数
     * @return 登录结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();
        
        // 使用print语句直接输出到控制台
        System.out.println("接收到登录请求，用户名: " + loginRequest.getUsername());
        System.out.println("接收到登录请求，密码: " + loginRequest.getPassword());
        
        // 根据用户名查找用户
        System.out.println("开始查找用户: " + loginRequest.getUsername());
        Optional<User> optionalUser = userService.findUserByUsername(loginRequest.getUsername());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            System.out.println("找到用户: " + user.getUsername() + "，ID: " + user.getId());
            System.out.println("数据库中存储的加密密码: " + user.getPassword());
            System.out.println("登录请求中的原始密码: " + loginRequest.getPassword());
            System.out.println("用户状态: " + user.getStatus());
            System.out.println("用户角色: " + user.getRole());
            
            // 尝试手动验证密码
            System.out.println("使用PasswordEncoder: " + passwordEncoder.getClass().getName() + " 进行密码验证");
            boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
            System.out.println("密码匹配结果: " + passwordMatches);
            
            // 额外测试：使用相同的PasswordEncoder加密原始密码并比较
            String encodedPassword = passwordEncoder.encode(loginRequest.getPassword());
            System.out.println("使用相同PasswordEncoder加密后的密码: " + encodedPassword);
            boolean testMatch = passwordEncoder.matches(loginRequest.getPassword(), encodedPassword);
            System.out.println("测试加密后的密码匹配结果: " + testMatch);
            
            // 验证用户状态
            if (user.getStatus() != 1) {
                System.out.println("用户状态不正常，当前状态: " + user.getStatus());
                response.put("success", false);
                response.put("message", "用户账号已被禁用");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            
            if (passwordMatches) {
                // 构建简化的用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("name", user.getRealName());
            userInfo.put("email", user.getEmail());
            userInfo.put("phone", user.getPhone());
            userInfo.put("role", user.getRole()); // 返回用户的实际角色信息
            
            // 如果是学生角色，尝试获取学号
            if ("student".equals(user.getRole())) {
                try {
                    // 通过userId查找学生信息
                    // 注意：这里假设student表中有userId字段，如果没有，需要修改查询逻辑
                    // 例如：可以通过user的其他属性（如用户名）来查找学生信息
                    // 目前暂时注释掉这部分代码，避免编译错误
                    Optional<Student> studentOptional = studentService.findStudentByUserId(user.getId());
                    if (studentOptional.isPresent()) {
                        Student student = studentOptional.get();
                        userInfo.put("studentNumber", student.getStudentNumber());
                        userInfo.put("name", student.getName());
                    }
                } catch (Exception e) {
                    System.out.println("获取学生学号失败: " + e.getMessage());
                }
            }
                
                // 生成JWT令牌，使用与CustomUserDetailsService相同的角色处理方式
                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase())))
                        .build();
                String token = jwtUtil.generateToken(userDetails);
                
                // 登录成功
                System.out.println("用户 " + user.getUsername() + " 登录成功");
                logger.info("用户 {} 登录成功", user.getUsername());
                response.put("success", true);
                response.put("message", "登录成功");
                response.put("user", userInfo);
                response.put("token", token);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                // 密码错误
                System.out.println("用户 " + user.getUsername() + " 登录失败，密码错误");
                logger.warn("用户 {} 登录失败，密码错误", user.getUsername());
                response.put("success", false);
                response.put("message", "密码错误");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } else {
            // 用户不存在
            System.out.println("登录失败，用户 " + loginRequest.getUsername() + " 不存在");
            logger.warn("登录失败，用户 {} 不存在", loginRequest.getUsername());
            response.put("success", false);
            response.put("message", "用户不存在");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 用户注册
     * @param registerRequest 注册请求参数
     * @return 注册结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest registerRequest) {
        Map<String, Object> response = new HashMap<>();
        
        // 检查用户名是否已存在
        if (userService.existsByUsername(registerRequest.getUsername())) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        // 检查邮箱是否已存在
        if (registerRequest.getEmail() != null && userService.existsByEmail(registerRequest.getEmail())) {
            response.put("success", false);
            response.put("message", "邮箱已被注册");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        // 加密密码
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRealName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setStatus(1); // 默认启用状态
        user.setRole(registerRequest.getRole()); // 使用前端传递的角色
        
        try {
            User savedUser = userService.createUser(user);
            response.put("success", true);
            response.put("message", "注册成功");
            response.put("user", savedUser);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "注册失败: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 登录请求参数
     */
    static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 注册请求参数
     */
    static class RegisterRequest {
        private String username;
        private String password;
        private String name;
        private String email;
        private String phone;
        private String role;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    /**
     * 获取当前登录用户信息
     * @return 当前用户信息
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取当前登录用户的用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> optionalUser = userService.findUserByUsername(username);
            
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                // 构建用户信息
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("name", user.getRealName());
                userInfo.put("email", user.getEmail());
                userInfo.put("phone", user.getPhone());
                userInfo.put("role", user.getRole());
                
                // 如果是学生角色，尝试获取学号
                if ("student".equals(user.getRole())) {
                    try {
                        Optional<Student> studentOptional = studentService.findStudentByUserId(user.getId());
                        if (studentOptional.isPresent()) {
                            Student student = studentOptional.get();
                            userInfo.put("studentNumber", student.getStudentNumber());
                            userInfo.put("name", student.getName());
                        }
                    } catch (Exception e) {
                        System.out.println("获取学生学号失败: " + e.getMessage());
                    }
                }
                
                response.put("success", true);
                response.put("message", "获取用户信息成功");
                response.put("user", userInfo);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("success", false);
                response.put("message", "用户不存在");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取用户信息失败: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}