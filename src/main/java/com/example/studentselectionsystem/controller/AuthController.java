package com.example.studentselectionsystem.controller;

import com.example.studentselectionsystem.entity.User;
import com.example.studentselectionsystem.service.VerificationCodeService;
import com.example.studentselectionsystem.service.UserService;
import com.example.studentselectionsystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 认证控制器，处理登录、注册、忘记密码等认证相关请求
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录结果，包含token和用户信息
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 验证用户名和密码
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            // 获取用户信息
            User user = userService.findUserByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            // 从Authentication获取UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // 生成JWT令牌
            String token = jwtUtil.generateToken(userDetails);

            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", token);
            response.put("user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "role", user.getRole(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "phone", user.getPhone()
            ));

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AuthenticationException e) {
            // 认证失败
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // 其他错误
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "登录失败，请联系管理员");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 用户注册
     * @param registerRequest 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest registerRequest) {
        try {
            // 检查用户名是否已存在
            if (userService.existsByUsername(registerRequest.getUsername())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户名已存在");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // 检查邮箱是否已存在
            if (userService.existsByEmail(registerRequest.getEmail())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "邮箱已被注册");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // 创建新用户
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setName(registerRequest.getName());
            user.setEmail(registerRequest.getEmail());
            user.setPhone(registerRequest.getPhone());
            user.setRole(registerRequest.getRole());

            // 保存用户
            User savedUser = userService.createUser(user);

            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "注册成功");
            response.put("user", Map.of(
                    "id", savedUser.getId(),
                    "username", savedUser.getUsername(),
                    "role", savedUser.getRole(),
                    "name", savedUser.getName(),
                    "email", savedUser.getEmail(),
                    "phone", savedUser.getPhone()
            ));

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            // 注册失败
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "注册失败：" + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 发送密码重置验证码
     * @param request 包含邮箱的请求体
     * @return 响应结果
     */
    @PostMapping("/send-reset-code")
    public ResponseEntity<String> sendResetCode(@RequestBody EmailRequest request) {
        // 检查邮箱是否存在
        boolean emailExists = userService.existsByEmail(request.getEmail());
        if (!emailExists) {
            return new ResponseEntity<>("邮箱不存在", HttpStatus.BAD_REQUEST);
        }

        // 发送验证码
        boolean sent = verificationCodeService.sendVerificationCode(request.getEmail());
        if (sent) {
            return new ResponseEntity<>("验证码发送成功", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("验证码发送失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 邮箱请求DTO
     */
    public static class EmailRequest {
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    /**
     * 验证重置密码验证码
     * @param email 邮箱
     * @param code 验证码
     * @return 验证结果
     */
    @PostMapping("/verify-reset-code")
    public ResponseEntity<Boolean> verifyResetCode(@RequestParam String email, @RequestParam String code) {
        boolean valid = verificationCodeService.verifyCode(email, code);
        return new ResponseEntity<>(valid, HttpStatus.OK);
    }

    /**
     * 重置密码
     * @param resetRequest 重置密码请求
     * @return 响应结果
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetRequest) {
        // 验证验证码
        boolean valid = verificationCodeService.verifyCode(resetRequest.getEmail(), resetRequest.getCode());
        if (!valid) {
            return new ResponseEntity<>("验证码无效或已过期", HttpStatus.BAD_REQUEST);
        }

        // 使用验证码
        boolean used = verificationCodeService.useCode(resetRequest.getEmail(), resetRequest.getCode());
        if (!used) {
            return new ResponseEntity<>("验证码使用失败", HttpStatus.BAD_REQUEST);
        }

        // 重置密码
        boolean reset = userService.resetPasswordByEmail(resetRequest.getEmail(), resetRequest.getNewPassword());
        if (reset) {
            return new ResponseEntity<>("密码重置成功", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("密码重置失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 登录请求DTO
     */
    public static class LoginRequest {
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
     * 注册请求DTO
     */
    public static class RegisterRequest {
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
     * 重置密码请求DTO
     */
    public static class ResetPasswordRequest {
        private String email;
        private String code;
        private String newPassword;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}
