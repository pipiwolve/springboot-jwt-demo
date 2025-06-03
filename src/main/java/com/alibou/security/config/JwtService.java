package com.alibou.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtService 用于生成和验证 JWT 令牌。
 * 负责对 Token 进行创建、解析、校验等操作。
 */
@Service
public class JwtService {

    // JWT 签名密钥（Base64 编码字符串）
    private static final String SECRET_KEY = "25yhNmDr0trA1fQezXpRN5tqlH2F/BpNa36KRpejI0JRHq/6ujfzPtsHhjs/xB1Q\n";

    /**
     * 根据用户信息生成 JWT Token，默认无额外声明
     * @param userDetails Spring Security 用户信息接口
     * @return 生成的 JWT Token 字符串
     */
    public String generateToken(UserDetails userDetails) {
        // 调用重载方法，传入空的额外声明
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * 从 JWT Token 中提取用户名（Subject）
     * @param token JWT Token
     * @return token 中的用户名
     */
    public String extractUsername(String token) {
        // 提取 subject 作为用户名
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 通用方法：根据传入的 Claims 解析函数，提取指定的 Claim
     * @param token JWT Token
     * @param claimsResolver 从 Claims 中提取指定字段的函数
     * @param <T> 泛型，返回值类型
     * @return 提取的 Claim 值
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token); // 解析所有 claims
        return claimsResolver.apply(claims); // 应用传入的提取函数
    }

    /**
     * 生成 JWT Token，带有额外的声明信息
     * @param extraClaims 额外自定义的 Claim 集合
     * @param userDetails 用户信息
     * @return 生成的 JWT Token
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        // 构造 JWT builder，设置 claims、subject、签发时间、过期时间，并签名
        return Jwts.builder()
                .setClaims(extraClaims) // 设置自定义声明
                .setSubject(userDetails.getUsername()) // 设置主题，即用户名
                .setIssuedAt(new Date(System.currentTimeMillis())) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // 过期时间（这里设为24分钟）
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // 签名算法及密钥
                .compact(); // 生成最终 token 字符串
    }

    /**
     * 验证 JWT Token 是否有效（用户名是否匹配且未过期）
     * @param token JWT Token
     * @param userDetails 用户信息
     * @return true 有效，false 无效
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token); // 提取 token 中的用户名
        // 用户名比对且 token 未过期才有效
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * 判断 Token 是否过期
     * @param token JWT Token
     * @return true 表示已过期，false 未过期
     */
    private boolean isTokenExpired(String token) {
        // 检查过期时间是否早于当前时间
        return extractExpiration(token).before(new Date());
    }

    /**
     * 从 Token 中提取过期时间
     * @param token JWT Token
     * @return 过期时间
     */
    private Date extractExpiration(String token) {
        // 提取 expiration claim
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 解析 JWT Token，提取所有声明信息
     * @param token JWT Token
     * @return Claims 声明集
     */
    private Claims extractClaims(String token) {
        // 使用签名密钥解析 token，获取 claims
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // 设置签名密钥
                .build()
                .parseClaimsJws(token) // 解析 token
                .getBody(); // 获取 Claims 内容
    }

    /**
     * 获取用于签名的密钥对象
     * @return 签名 Key
     */
    private Key getSignInKey() {
        // 将 Base64 编码的密钥解码为字节数组
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        // 生成 HMAC SHA 密钥对象
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
