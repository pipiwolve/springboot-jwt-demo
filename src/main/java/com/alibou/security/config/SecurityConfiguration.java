package com.alibou.security.config;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // （可选）匹配所有路径，可省略，默认即为全匹配
                .securityMatcher("/**")
                // 关闭 CSRF（跨站请求伪造）保护，适用于 JWT 无状态认证
                .csrf(csrf -> csrf.disable())
                // 设置会话策略为无状态（每个请求都需重新认证）
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 配置请求授权规则
                .authorizeHttpRequests(auth -> auth
                        // 放行认证接口（如登录/注册）
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // 其他请求必须认证
                        .anyRequest().authenticated()
                )
                // 设置认证提供者，用于校验用户名密码
                .authenticationProvider(authenticationProvider)
                // 添加 JWT 过滤器，放在用户名密码过滤器之前
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // 构建最终的 SecurityFilterChain 对象
        return http.build();
    }
}
