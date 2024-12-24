# GIRA 后端详细设计文档

## 1. 项目结构

### 1.1 模块划分
```
gira-backend/
├── gira-common/          # 公共模块
├── gira-core/            # 核心业务模块
├── gira-auth/            # 认证授权模块
├── gira-api/             # API接口模块
└── gira-admin/           # 管理后台模块
```

### 1.2 包结构
```
com.rayfay.gira/
├── common/               # 公共工具类
├── config/               # 配置类
├── constant/             # 常量定义
├── controller/           # 控制器
├── service/             # 服务层
│   ├── impl/           # 服务实现
│   └── facade/         # 服务门面
├── repository/          # 数据访问层
├── domain/             # 领域模型
│   ├── entity/        # 实体类
│   ├── vo/            # 值对象
│   └── dto/           # 数据传输对象
├── exception/          # 异常处理
└── util/              # 工具类
```

## 2. 数据库设计

### 2.1 用户认证相关表
```sql
-- 用户表
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100),
    avatar_url VARCHAR(255),
    status SMALLINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 角色表
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 用户角色关联表
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

-- 权限表
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 角色权限关联表
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL REFERENCES roles(id),
    permission_id BIGINT NOT NULL REFERENCES permissions(id),
    PRIMARY KEY (role_id, permission_id)
);
```

### 2.2 项目管理相关表
```sql
-- 项目表
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    key VARCHAR(10) NOT NULL UNIQUE,
    description TEXT,
    owner_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 问题类型表
CREATE TABLE issue_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    icon VARCHAR(255),
    description VARCHAR(255),
    project_id BIGINT NOT NULL REFERENCES projects(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 问题状态表
CREATE TABLE issue_status (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    category VARCHAR(20) NOT NULL, -- TODO, IN_PROGRESS, DONE
    project_id BIGINT NOT NULL REFERENCES projects(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 问题表
CREATE TABLE issues (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type_id BIGINT NOT NULL REFERENCES issue_types(id),
    status_id BIGINT NOT NULL REFERENCES issue_status(id),
    project_id BIGINT NOT NULL REFERENCES projects(id),
    reporter_id BIGINT NOT NULL REFERENCES users(id),
    assignee_id BIGINT REFERENCES users(id),
    priority SMALLINT NOT NULL DEFAULT 3,
    due_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 问题评论表
CREATE TABLE issue_comments (
    id BIGSERIAL PRIMARY KEY,
    issue_id BIGINT NOT NULL REFERENCES issues(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 附件表
CREATE TABLE attachments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    path VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    issue_id BIGINT NOT NULL REFERENCES issues(id),
    uploader_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## 3. 核心服务设计

### 3.1 认证服务
```java
@Service
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    public AuthenticationResponse login(LoginRequest request) {
        // 用户认证逻辑
    }
    
    public void register(RegisterRequest request) {
        // 用户注册逻辑
    }
    
    public void logout(String token) {
        // 登出逻辑
    }
}
```

### 3.2 问题管理服务
```java
@Service
public class IssueService {
    
    private final IssueRepository issueRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    
    @Transactional
    public Issue createIssue(IssueCreateRequest request) {
        // 创建问题逻辑
    }
    
    @Transactional
    public Issue updateIssue(Long issueId, IssueUpdateRequest request) {
        // 更新问题逻辑
    }
    
    @Transactional
    public void deleteIssue(Long issueId) {
        // 删除问题逻辑
    }
}
```

### 3.3 项目管理服务
```java
@Service
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    private final UserService userService;
    
    @Transactional
    public Project createProject(ProjectCreateRequest request) {
        // 创建项目逻辑
    }
    
    public List<Project> getUserProjects(Long userId) {
        // 获取用户项目列表逻辑
    }
}
```

## 4. 安全配置

### 4.1 Spring Security配置
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/auth/**").permitAll()
            .antMatchers("/api/**").authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

### 4.2 JWT配置
```java
@Configuration
public class JwtConfig {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private long expiration;
    
    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(secret, expiration);
    }
}
```

## 5. 缓存策略

### 5.1 Redis缓存配置
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
            
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build();
    }
}
```

### 5.2 缓存使用
```java
@Service
public class ProjectService {
    
    @Cacheable(value = "projects", key = "#projectId")
    public Project getProject(Long projectId) {
        return projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
    }
    
    @CacheEvict(value = "projects", key = "#projectId")
    public void updateProject(Long projectId, ProjectUpdateRequest request) {
        // 更新项目逻辑
    }
}
```

## 6. 异常处理

### 6.1 全局异常处理
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
```

## 7. 性能优化

### 7.1 数据库优化
- 索引设计
- 分页查询
- 批量操作
- 延迟加载

### 7.2 缓存优化
- 多级缓存
- 缓存预热
- 缓存更新策略

### 7.3 并发处理
- 线程池配置
- 异步处理
- 分布式锁

## 8. 监控和日志

### 8.1 Actuator配置
```java
@Configuration
public class ActuatorConfig {
    
    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
}
```

### 8.2 日志配置
```yaml
logging:
  level:
    root: INFO
    com.rayfay.gira: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/gira.log
    max-size: 10MB
    max-history: 30
``` 