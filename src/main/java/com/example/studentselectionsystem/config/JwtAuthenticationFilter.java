package com.example.studentselectionsystem.config;

import com.example.studentselectionsystem.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器，用于验证请求中的JWT令牌
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    private final String jwtHeader = "Authorization";

    /**
     * 确定是否应该跳过过滤
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        
        // 构建跳过规则，只跳过特定的GET请求，不跳过POST请求
        boolean shouldSkip = requestURI.equals("/api/auth/login") || 
                           requestURI.equals("/api/auth/register") ||
                           requestURI.startsWith("/api/test/") ||
                           requestURI.startsWith("/api/public/") ||
                           // 跳过学生相关公开接口 - 仅跳过GET请求
                           (requestURI.equals("/api/students") && requestMethod.equals("GET")) ||
                           (requestURI.equals("/api/students/count") && requestMethod.equals("GET")) ||
                           (requestURI.equals("/api/students/with-major") && requestMethod.equals("GET")) ||
                           
                           // 跳过课程相关公开接口 - 仅跳过GET请求
                           (requestURI.equals("/api/courses/with-teacher") && requestMethod.equals("GET")) ||
                           (requestURI.equals("/api/courses/page") && requestMethod.equals("GET")) ||
                           // 跳过专业相关公开接口 - 仅跳过GET请求
                           (requestURI.equals("/api/majors/search") && requestMethod.equals("GET")) ||
                           // 跳过评奖标准相关公开接口 - 仅跳过GET请求
                           (requestURI.equals("/api/standards/page") && requestMethod.equals("GET")) ||
                           // 跳过统计类公开接口 - 仅跳过GET请求
                           (requestURI.equals("/api/awards/count") && requestMethod.equals("GET")) ||
                           (requestURI.equals("/api/student-award-applications/count") && requestMethod.equals("GET")) ||
                           // 跳过最近记录类公开接口 - 仅跳过GET请求
                           (requestURI.equals("/api/awards/recent") && requestMethod.equals("GET")) ||
                           (requestURI.equals("/api/notices/recent") && requestMethod.equals("GET"));
        System.out.println("JwtAuthenticationFilter - shouldNotFilter: " + shouldSkip + " for URI: " + requestURI);
        return shouldSkip;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 添加调试日志
        System.out.println("\n" + "=" .repeat(50));
        System.out.println("JwtAuthenticationFilter - 请求URL: " + request.getRequestURL());
        System.out.println("JwtAuthenticationFilter - 请求方法: " + request.getMethod());
        
        // 获取请求头中的Authorization信息
        final String authorizationHeader = request.getHeader(jwtHeader);
        System.out.println("JwtAuthenticationFilter - Authorization头: " + authorizationHeader);

        String username = null;
        String jwt = null;

        // 检查Authorization头是否存在且以Bearer开头
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
            System.out.println("JwtAuthenticationFilter - 提取的用户名: " + username);
        }

        // 如果JWT令牌有效且SecurityContext中没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("JwtAuthenticationFilter - 准备验证JWT令牌");
            
            try {
                // 加载用户信息
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("JwtAuthenticationFilter - 加载的用户信息: " + userDetails.getUsername());
                System.out.println("JwtAuthenticationFilter - 用户权限: " + userDetails.getAuthorities());
                
                // 验证JWT令牌
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    System.out.println("JwtAuthenticationFilter - JWT令牌验证成功");
                    
                    // 从JWT令牌中提取角色信息
                    List<GrantedAuthority> authorities = jwtUtil.extractRoles(jwt);
                    System.out.println("JwtAuthenticationFilter - 从JWT令牌中提取的角色: " + authorities);
                    
                    // 创建认证令牌 - 使用从JWT令牌中提取的角色信息
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, authorities);
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 将认证信息设置到SecurityContext中 - 使用createEmptyContext()创建新的上下文
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(context);
                
                System.out.println("JwtAuthenticationFilter - 认证信息已设置到SecurityContext中");
                System.out.println("JwtAuthenticationFilter - 认证对象: " + SecurityContextHolder.getContext().getAuthentication());
                System.out.println("JwtAuthenticationFilter - 认证对象权限: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                System.out.println("JwtAuthenticationFilter - 认证对象是否已认证: " + SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
                System.out.println("JwtAuthenticationFilter - 用户: " + userDetails.getUsername());
                System.out.println("JwtAuthenticationFilter - 角色: " + userDetails.getAuthorities());
                System.out.println("JwtAuthenticationFilter - JWT认证成功，设置认证信息完成");
                } else {
                    System.out.println("JwtAuthenticationFilter - JWT令牌验证失败");
                }
            } catch (Exception e) {
                System.out.println("JwtAuthenticationFilter - 处理JWT令牌时发生异常: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("JwtAuthenticationFilter - 无需进行JWT验证");
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                System.out.println("JwtAuthenticationFilter - 已有认证信息: " + SecurityContextHolder.getContext().getAuthentication());
                System.out.println("JwtAuthenticationFilter - 已有认证信息是否已认证: " + SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
                System.out.println("JwtAuthenticationFilter - 已有认证信息权限: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            }
        }

        // 继续过滤器链
        System.out.println("JwtAuthenticationFilter - 继续过滤器链");
        filterChain.doFilter(request, response);
    }
}
