非常好！现在我们来系统梳理你的 **Spring Boot + JWT 安全认证项目**的完整工作流，包括：



- 各个模块之间的调用关系
- 每个类/方法的职责
- 整个认证流程（注册 + 登录 + 请求受保护资源）

------





## **🧩 一、模块总览（包结构）**



```
com.alibou.security
├── auth             // 用户认证：注册、登录请求处理
├── config           // 安全配置：JWT、SecurityFilterChain 等
├── demo             // 示例接口（受保护资源）
├── user             // 用户模型、角色、持久化操作
└── SecurityApplication.java // 应用启动入口
```



------





## **🧠 二、工作流主线（详细步骤）**







### **1️⃣ 注册流程（POST** 

### **/api/v1/auth/register**

### **）**







#### **1.1 控制器接收请求**



```
@PostMapping("/register")
public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request)
```



- 接收前端发来的注册数据（email, password, firstname, lastname）







#### **1.2 调用** 

#### **AuthenticationService.register()**



```
public AuthenticationResponse register(RegisterRequest request) {
    // 构建用户对象并加密密码
    User user = User.builder()
        .email(...)
        .password(passwordEncoder.encode(...)) // 密码加密
        .build();

    repository.save(user); // 保存用户到数据库

    String jwtToken = jwtService.generateToken(user); // 为新用户生成 token
    return AuthenticationResponse.builder().token(jwtToken).build();
}
```



#### **✅ 注册结果**





- 用户数据保存入库
- 返回带有 JWT Token 的响应，前端存储该 token





------





### **2️⃣ 登录流程（POST** 

### **/api/v1/auth/authenticate**

### **）**







#### **2.1 控制器接收请求**



```
@PostMapping("/authenticate")
public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request)
```



- 接收用户登录邮箱和密码







#### **2.2 调用** 

#### **AuthenticationService.authenticate()**



```
authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(email, password)
);
```



- 使用 AuthenticationManager 验证用户名密码

- 背后会调用：

  

  - userDetailsService.loadUserByUsername(email)
  - 从数据库查找用户
  - 使用 PasswordEncoder.matches(...) 校验密码

  







#### **2.3 成功后生成 JWT**



```
String jwtToken = jwtService.generateToken(user);
```



- 使用用户信息生成 token，返回给前端





------





### **3️⃣ 前端访问受保护资源（携带 token）**







#### **示例：**



```
GET /api/v1/demo-controller/hello
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR...
```



------





## **🔒 三、请求经过过滤器链认证流程**







### **JwtAuthenticationFilter.java**



```
String jwt = request.getHeader("Authorization").substring(7);
String username = jwtService.extractUsername(jwt);
```



#### **步骤详解：**





1. 过滤器从请求头提取 JWT
2. 调用 jwtService.extractUsername() 解析出用户名（subject）
3. 若上下文中未认证，调用：

```
UserDetails userDetails = userDetailsService.loadUserByUsername(username);
```



校验 token 是否有效：

```
jwtService.isTokenValid(token, userDetails)
```



构建认证对象，设置进 Spring Security 的上下文：

```
UsernamePasswordAuthenticationToken(...)
SecurityContextHolder.getContext().setAuthentication(...)
```

------





## **⚙️ 四、配置核心模块：ApplicationConfig.java**







### **主要定义了：**



| **Bean**               | **说明**                                                     |
| ---------------------- | ------------------------------------------------------------ |
| UserDetailsService     | 根据用户名查数据库中的用户信息                               |
| AuthenticationProvider | 指定 UserDetailsService + PasswordEncoder，供 Spring Security 使用 |
| AuthenticationManager  | 提供标准认证接口                                             |
| PasswordEncoder        | 使用 BCrypt 进行密码加密与验证                               |



------





## **🔐 五、SecurityConfiguration.java**





配置 Spring Security 的安全规则：

```
http
  .csrf().disable()
  .sessionManagement().sessionCreationPolicy(STATELESS)
  .authorizeHttpRequests()
      .requestMatchers("/api/v1/auth/**").permitAll() // 登录注册允许
      .anyRequest().authenticated()
  .authenticationProvider(authenticationProvider)
  .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
```



------





## **🗂️ 六、模块职责对照表**



| **模块**                | **主要职责**                                 |
| ----------------------- | -------------------------------------------- |
| auth.*                  | 控制器接收注册/登录请求，返回 JWT            |
| user.*                  | 用户实体、角色、数据库操作                   |
| JwtService              | 生成、解析、验证 JWT                         |
| JwtAuthenticationFilter | 拦截请求，提取并验证 token，设置用户认证状态 |
| SecurityConfiguration   | 设置访问规则、添加过滤器                     |
| ApplicationConfig       | 定义核心 Bean（认证、加密、用户查找）        |
| AuthenticationManager   | 校验用户名密码的统一接口                     |



------





## **✅ 七、整体流程图（简述）**



```
[客户端注册/登录] 
      ↓
[AuthController] → [AuthenticationService]
      ↓                     ↓
  JWTService           UserRepository
      ↓                     ↓
 返回 JWT ←←← 保存 / 验证用户

【客户端携带 JWT 请求受保护接口】
      ↓
[JwtAuthenticationFilter]
      ↓
[JwtService.extractUsername + 校验 Token]
      ↓
[UserDetailsService 加载用户]
      ↓
[SecurityContext 设置认证用户]
      ↓
[Controller 接收请求，已认证用户可访问]
```



------





## **🧠 补充建议**





- ✅ 你已实现一个标准、清晰的 JWT 登录系统。

- ✅ 项目结构解耦良好，便于扩展和维护。

- 可拓展功能包括：

  

  - refresh token
  - token 黑名单
  - 邮箱验证/密码找回

  

