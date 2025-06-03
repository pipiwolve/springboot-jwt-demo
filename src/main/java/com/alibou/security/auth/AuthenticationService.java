package com.alibou.security.auth;

import com.alibou.security.config.JwtService;
import com.alibou.security.user.Role;
import com.alibou.security.user.User;
import com.alibou.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务类，处理用户注册和登录的业务逻辑。
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository; // 用户数据操作接口
    private final PasswordEncoder passwordEncoder; // 密码加密器
    private final JwtService jwtService; // JWT 生成与验证服务
    private final AuthenticationManager authenticationManager; // Spring Security 的认证管理器


    /**
     * 用户注册逻辑：
     * - 构建用户对象
     * - 加密密码
     * - 保存到数据库
     * - 生成 JWT Token 返回
     */
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFisrstname()) // 设置用户名
                .lastname(request.getLastname())    // 设置姓氏
                .email(request.getEmail())          // 设置邮箱（用户名）
                .password(passwordEncoder.encode(request.getPassword())) // 加密密码
                .role(Role.USER)                    // 默认角色为 USER
                .build();
        repository.save(user); // 保存用户到数据库
        var jwtToken = jwtService.generateToken(user); // 为用户生成 JWT
        return AuthenticationResponse.builder()
                .token(jwtToken) // 返回 token
                .build();
    }

    /**
     * 用户登录逻辑：
     * - 使用 AuthenticationManager 验证用户凭证
     * - 获取用户信息
     * - 生成并返回 JWT Token
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),     // 传入用户名（邮箱）
                        request.getPassword()   // 传入密码
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(); // 从数据库获取用户信息
        var jwtToken = jwtService.generateToken(user); // 为用户生成 JWT
        return AuthenticationResponse.builder()
                .token(jwtToken) // 返回 token
                .build();

    }
}
