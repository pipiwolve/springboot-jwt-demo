package com.alibou.security.auth;

/**
 * 注册请求对象，封装了用户注册时提交的基本信息。
 */
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String fisrstname; // 用户名 - 名
    private String lastname;   // 用户名 - 姓
    private String email;      // 用户邮箱
    private String password;   // 用户密码（明文，后续需加密处理）

}
