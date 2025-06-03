package com.alibou.security.config;

import com.alibou.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 应用级别的配置类，定义了 Spring Security 相关的核心 Bean。
 * 包括：用户详情服务、认证提供者、认证管理器、密码加密器。
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository repository;

    /**
     * 定义用户详情服务（UserDetailsService）
     * 用于根据用户名（email）从数据库加载用户信息
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Lambda 实现接口，根据用户名查询用户，找不到则抛出异常
        return username -> repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("USER NOT FOUND"));
    }

    /**
     * 定义认证提供者（AuthenticationProvider）
     * 用于指定用户详情服务与密码加密器，配合 Spring Security 使用
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authprovider = new DaoAuthenticationProvider();
        authprovider.setUserDetailsService(userDetailsService()); // 设置用户详情服务
        authprovider.setPasswordEncoder(passwordEncoder());       // 设置密码加密器
        return authprovider;
    }

    /**
     * 定义认证管理器（AuthenticationManager）
     * 用于处理 Spring Security 的认证请求（登录时使用）
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 定义密码加密器（PasswordEncoder）
     * 使用 BCrypt 加密算法
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
