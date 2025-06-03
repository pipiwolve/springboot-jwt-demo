package com.alibou.security.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * User entity representing an application user.
 * Implements Spring Security's UserDetails interface to provide
 * necessary user information to the security framework.
 */
@Data // Lombok 注解，自动生成 getter/setter、toString、equals、hashCode 方法
@Builder // Lombok 注解，支持链式构建对象
@NoArgsConstructor // 生成无参构造方法
@AllArgsConstructor // 生成全参构造方法
@Entity // JPA 注解，表示该类是一个实体类
@Table(name = "_user") // 映射到数据库中的 "_user" 表
public class User implements UserDetails {

    @Id
    @GeneratedValue // 主键自增
    private Integer id;

    private String firstname; // 用户名 - 名
    private String lastname;  // 用户名 - 姓
    private String email;     // 用户邮箱，作为用户名（username）使用
    private String password;  // 加密后的密码

    @Enumerated(EnumType.STRING) // 将枚举以字符串形式存储
    private Role role; // 用户角色（如 ADMIN、USER）

    /**
     * 返回当前用户的权限集合，这里只返回一个：根据角色创建的 SimpleGrantedAuthority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    /**
     * 返回用户密码（由 Spring Security 自动调用）
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * 返回用户名（这里我们用 email 作为用户名）
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * 帐号是否未过期（true 表示有效）
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 帐号是否未锁定（true 表示未锁）
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 凭证（密码）是否未过期（true 表示有效）
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 用户是否启用（true 表示已启用）
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
