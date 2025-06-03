package com.alibou.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 提供注册和登录接口的控制器类。
 * 接口路径统一以 "/api/v1/auth" 开头。
 */
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    /**
     * 注册接口
     * 请求方式：POST
     * 路径：/api/v1/auth/register
     * 请求体：RegisterRequest（包含邮箱、密码、姓名等信息）
     * 返回：包含 JWT Token 的 AuthenticationResponse
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
        return ResponseEntity.ok(service.register(request)); // 调用服务注册方法
    }

    /**
     * 登录接口
     * 请求方式：POST
     * 路径：/api/v1/auth/authenticate
     * 请求体：AuthenticationRequest（邮箱 + 密码）
     * 返回：包含 JWT Token 的 AuthenticationResponse
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(service.authenticate(request)); // 调用服务登录方法
    }
}
