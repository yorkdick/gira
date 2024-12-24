# GIRA API接口设计文档

## 1. API概述

### 1.1 基本信息
- 基础URL: `http://api.gira.com/v1`
- 认证方式: JWT Token + Spring Security
- 响应格式: JSON
- 时间格式: ISO 8601 (YYYY-MM-DDTHH:mm:ss.sssZ)
- API文档: Swagger/OpenAPI 3.0

### 1.2 通用响应格式
```json
{
    "code": 200,
    "message": "success",
    "data": {}
}
```

### 1.3 错误码说明
- 200: 成功
- 400: 请求参数错误
- 401: 未认证
- 403: 无权限
- 404: 资源不存在
- 500: 服务器错误

## 2. 认证接口

### 2.1 用户登录
- **POST** `/auth/login`
- Controller注解: `@RestController`
- 方法注解: `@PostMapping("/auth/login")`
- 请求体:
```java
public class LoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private boolean remember;
}
```
- 响应体:
```java
public class LoginResponse {
    private String token;
    private UserDTO user;
}
```

### 2.2 用户注册
- **POST** `/auth/register`
- Controller注解: `@RestController`
- 方法注解: `@PostMapping("/auth/register")`
- 请求体:
```java
public class RegisterRequest {
    @NotBlank
    @Size(min = 4, max = 50)
    private String username;
    
    @NotBlank
    @Size(min = 6, max = 100)
    private String password;
    
    @NotBlank
    @Email
    private String email;
}
```

### 2.3 退出登录
- **POST** `/auth/logout`
- 请求头: `Authorization: Bearer {token}`
- 安全注解: `@PreAuthorize("isAuthenticated()")`

## 3. Backlog接口

### 3.1 需求管理

#### 3.1.1 创建需求
- **POST** `/backlog/issues`
- Controller注解: `@RestController`
- 方法注解: `@PostMapping("/backlog/issues")`
- 安全注解: `@PreAuthorize("hasRole('USER')")`
- 请求体:
```java
public class IssueRequest {
    @NotBlank
    private String title;
    
    @NotBlank
    private String description;
    
    @Min(1) @Max(5)
    private Integer priority;
    
    private String assignee;
    
    @Future
    private LocalDateTime dueDate;
    
    private List<String> labels;
    
    private List<String> attachments;
}
```

#### 3.1.2 获取需求列表
- **GET** `/backlog/issues`
- 方法注解: `@GetMapping("/backlog/issues")`
- 分页注解: `@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)`
- 查询参数:
```java
@RequestParam(required = false) String filter
@PageableDefault(size = 20) Pageable pageable
```

#### 3.1.3 更新需求
- **PUT** `/backlog/issues/{issueId}`
- 方法注解: `@PutMapping("/backlog/issues/{issueId}")`
- 路径变量: `@PathVariable Long issueId`
- 验证注解: `@Valid @RequestBody IssueUpdateRequest request`

#### 3.1.4 删除需求
- **DELETE** `/backlog/issues/{issueId}`
- 方法注解: `@DeleteMapping("/backlog/issues/{issueId}")`
- 安全注解: `@PreAuthorize("hasRole('ADMIN')")`

## 4. 看板接口

### 4.1 看板管理

#### 4.1.1 创建看板
- **POST** `/boards`
- Controller注解: `@RestController`
- 方法注解: `@PostMapping("/boards")`
- 请求体:
```java
public class BoardRequest {
    @NotBlank
    private String name;
    
    private String description;
    
    @Valid
    private List<BoardColumnRequest> columns;
}
```

#### 4.1.2 获取看板列表
- **GET** `/boards`
- 方法注解: `@GetMapping("/boards")`
- 分页参数: `Pageable pageable`

#### 4.1.3 获取看板详情
- **GET** `/boards/{boardId}`
- 方法注解: `@GetMapping("/boards/{boardId}")`
- 缓存注解: `@Cacheable(value = "boards", key = "#boardId")`

#### 4.1.4 更新任务状态
- **PUT** `/boards/{boardId}/issues/{issueId}`
- 方法注解: `@PutMapping("/boards/{boardId}/issues/{issueId}")`
- 事务注解: `@Transactional`

## 5. 用户接口

### 5.1 用户管理

#### 5.1.1 获取用户信息
- **GET** `/users/{userId}`
- 方法注解: `@GetMapping("/users/{userId}")`
- 安全注解: `@PreAuthorize("hasRole('USER')")`

#### 5.1.2 更新用户信息
- **PUT** `/users/{userId}`
- 方法注解: `@PutMapping("/users/{userId}")`
- 验证注解: `@Valid`

### 5.2 团队管理

#### 5.2.1 创建团队
- **POST** `/teams`
- 方法注解: `@PostMapping("/teams")`
- 事务注解: `@Transactional`

#### 5.2.2 添加团队成员
- **POST** `/teams/{teamId}/members`
- 方法注解: `@PostMapping("/teams/{teamId}/members")`
- 安全注解: `@PreAuthorize("hasRole('TEAM_ADMIN')")`

## 6. 系统配置接口

### 6.1 项目设置

#### 6.1.1 获取项目配置
- **GET** `/settings/project`
- 方法注解: `@GetMapping("/settings/project")`
- 缓存注解: `@Cacheable("projectSettings")`

#### 6.1.2 更新项目配置
- **PUT** `/settings/project`
- 方法注解: `@PutMapping("/settings/project")`
- 缓存注解: `@CacheEvict(value = "projectSettings", allEntries = true)`

### 6.2 权限管理

#### 6.2.1 角色配置
- **POST** `/settings/roles`
- 方法注解: `@PostMapping("/settings/roles")`
- 安全注解: `@PreAuthorize("hasRole('ADMIN')")`

#### 6.2.2 获取权限列表
- **GET** `/settings/permissions`
- 方法注解: `@GetMapping("/settings/permissions")`
- 缓存注解: `@Cacheable("permissions")`

## 7. 通用接口

### 7.1 文件上传
- **POST** `/files/upload`
- 方法注解: `@PostMapping("/files/upload")`
- 请求注解: `@RequestParam("file") MultipartFile file`
- 验证注解: `@ValidFile`

### 7.2 搜索接口
- **GET** `/search`
- 方法注解: `@GetMapping("/search")`
- 分页注解: `@PageableDefault` 