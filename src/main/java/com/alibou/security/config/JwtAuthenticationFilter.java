package com.alibou.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器，继承 OncePerRequestFilter，确保每个请求只执行一次。
 * 核心职责：
 * - 从请求头中提取 JWT
 * - 解析并验证 token 的合法性
 * - 若合法，则将用户信息放入 Spring Security 的上下文中，实现认证通过
 */
@Component
@RequiredArgsConstructor // Lombok 注解：自动为 final 字段生成构造函数
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService; // 用于处理 JWT 的服务类
    private final UserDetailsService userDetailsService; // 用于根据用户名加载用户信息

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization"); // 从请求头获取 Authorization 字段
        final String jwt;
        final String userEmail;

        // 若没有 token 或 token 不以 "Bearer " 开头，直接放行
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 提取 JWT 字符串（去掉前缀 "Bearer "）
        jwt = authHeader.substring(7);

        // 从 JWT 中提取用户名（email）
        userEmail = jwtService.extractUsername(jwt);

        // 如果用户名不为空，且当前没有认证信息（避免重复设置）
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 从数据库加载用户信息（Spring Security 会用这个用户信息来构造 Authentication）
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 检查 JWT 是否有效（是否与用户匹配，是否过期等）
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // 构造 Spring Security 的认证对象
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                // 设置附加的认证细节（如请求来源 IP 等）
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // 将认证对象设置进 SecurityContextHolder，使用户处于“已认证”状态
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 继续处理过滤器链
        filterChain.doFilter(request, response);
    }
}
