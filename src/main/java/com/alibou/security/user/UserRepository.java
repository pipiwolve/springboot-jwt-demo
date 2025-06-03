package com.alibou.security.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 */
@Repository // 将该接口标记为 Spring 的 Repository（用于自动注入）
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * 根据用户邮箱查找用户信息
     * @param email 用户的邮箱地址
     * @return 包含用户信息的 Optional，如果不存在则为空
     */
    Optional<User> findByEmail(String email);
}
