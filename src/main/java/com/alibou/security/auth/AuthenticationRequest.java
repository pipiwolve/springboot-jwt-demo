package com.alibou.security.auth;

/**
 * 登录请求对象，封装用户登录所需的凭据信息。
 */
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    private String email;    // 用户邮箱，用作登录账号
    private String password; // 用户密码，明文形式（后续由 Spring Security 认证）

}
