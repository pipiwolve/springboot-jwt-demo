package com.alibou.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 应用入口类，是整个 Spring Boot 应用的启动引导类
@SpringBootApplication // 组合注解：包含 @Configuration, @EnableAutoConfiguration, @ComponentScan
public class SecurityApplication {

	public static void main(String[] args) {
		// 启动 Spring Boot 应用
		SpringApplication.run(SecurityApplication.class, args);
	}
}
