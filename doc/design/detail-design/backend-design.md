# GIRA后台详细设计文档

## 1. 系统架构设计

### 1.1 技术栈选型
- 核心框架：Spring Boot 3.2.0
- JDK版本：OpenJDK 21
- 安全框架：Spring Security + JWT
- 数据访问：Spring Data JPA
- 数据库：PostgreSQL 14
- 对象映射：MapStruct 1.5.5.Final
- 代码简化：Lombok 1.18.30
- API文档：SpringDoc OpenAPI 2.3.0

### 1.2 系统分层
1. 表现层（Controller）
   - REST API接口实现
   - 请求参数验证
   - 响应数据封装

2. 业务层（Service）
   - 业务逻辑处理
   - 事务管理
   - 权限校验

3. 数据访问层（Repository）
   - 数据库操作
   - 查询优化

4. 公共层（Common）
   - 工具类
   - 常量定义
   - 异常处理

## 2. 核心功能模块设计

### 2.1 认证与权限模块

#### 2.1.1 角色定义
```java
public enum UserRole {
    ADMIN,      // 管理员
    DEVELOPER   // 开发者
}
```

#### 2.1.2 权限控制
```java
// 管理员权限
@PreAuthorize("hasRole('ADMIN')")

// 开发者或管理员权限
@PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER')")

// 资源所有者权限
@PreAuthorize("@securityService.isResourceOwner(#taskId)")
```

#### 2.1.3 认证接口
1. 用户登录
- 路径: `POST /api/auth/login`
- 功能: 用户登录并获取访问令牌
- 权限: 无需权限
- 请求体:
```json
{
    "username": "string",
    "password": "string"
}
```
- 响应体:
```json
{
    "token": "string",
    "tokenType": "Bearer",
    "expiresIn": "number"
}
```
- 状态码:
  - 200: 登录成功
  - 401: 用户名或密码错误
  - 403: 账户已被禁用

2. 密码修改
- 路径: `PUT /api/users/{id}/password`
- 功能: 修改用户密码
- 权限: 用户本人或管理员
- 请求体:
```json
{
    "oldPassword": "string",
    "newPassword": "string"
}
```
- 状态码:
  - 200: 修改成功
  - 400: 密码格式错误
  - 401: 原密码错误
  - 403: 无权限

3. 创建用户
- 路径: `POST /api/users`
- 功能: 管理员创建新用户
- 权限: 仅管理员
- 请求体:
```json
{
    "username": "string",
    "email": "string",
    "fullName": "string",
    "password": "string"
}
```
- 响应体:
```json
{
    "id": "number",
    "username": "string",
    "email": "string",
    "fullName": "string",
    "role": "DEVELOPER",
    "createdAt": "string"
}
```
- 状态码:
  - 201: 创建成功
  - 400: 请求参数错误
  - 403: 无权限
  - 409: 用户名已存在

4. 获取用户列表
- 路径: `GET /api/users`
- 功能: 获取用户列表
- 权限: 所有用户
- 查询参数:
  - page: 页码（从0开始）
  - size: 每页大小
  - sort: 排序字段
- 响应体:
```json
{
    "content": [
        {
            "id": "number",
            "username": "string",
            "email": "string",
            "fullName": "string",
            "role": "string"
        }
    ],
    "totalElements": "number",
    "totalPages": "number",
    "size": "number",
    "number": "number"
}
```

5. 更新用户信息
- 路径: `PUT /api/users/{id}`
- 功能: 更新用户基本信息
- 权限: 用户本人或管理员
- 请求体:
```json
{
    "email": "string",
    "fullName": "string"
}
```
- 状态码:
  - 200: 更新成功
  - 400: 请求参数错误
  - 403: 无权限
  - 404: 用户不存在

### 2.2 看板模块

#### 2.2.1 数据模型
1. 看板状态
```java
public enum BoardStatus {
    ACTIVE,     // 活动状态
    ARCHIVED    // 已归档
}
```

2. 看板列状态
```java
public class ColumnStatus {
    private int taskCount;        // 任务数量
    private int wipLimit;         // 在制品限制
    private boolean isOverLimit;  // 是否超出限制
}
```

#### 2.2.2 看板接口
1. 创建看板
- 路径: `POST /api/boards`
- 功能: 创建新的看板
- 权限: 仅管理员
- 请求体:
```json
{
    "name": "string",
    "description": "string",
    "columns": [
        {
            "name": "string",
            "orderIndex": "number",
            "wipLimit": "number"
        }
    ]
}
```
- 响应体:
```json
{
    "id": "number",
    "name": "string",
    "description": "string",
    "columns": [
        {
            "id": "number",
            "name": "string",
            "orderIndex": "number",
            "wipLimit": "number"
        }
    ],
    "createdBy": "number",
    "createdAt": "string"
}
```
- 状态码:
  - 201: 创建成功
  - 400: 请求参数错误
  - 403: 无权限

2. 更新看板列
- 路径: `PUT /api/boards/{id}/columns`
- 功能: 更新看板列配置
- 权限: 仅管理员
- 请求体:
```json
{
    "columns": [
        {
            "id": "number",
            "name": "string",
            "orderIndex": "number",
            "wipLimit": "number"
        }
    ]
}
```
- 状态码:
  - 200: 更新成功
  - 400: 请求参数错误
  - 403: 无权限
  - 404: 看板不存在

3. 获取看板列表
- 路径: `GET /api/boards`
- 功能: 获取所有看板
- 权限: 所有用户
- 查询参数:
  - status: 看板状态（可选）
- 响应体:
```json
[
    {
        "id": "number",
        "name": "string",
        "description": "string",
        "status": "string",
        "createdAt": "string"
    }
]
```

4. 获取看板详情
- 路径: `GET /api/boards/{id}`
- 功能: 获取看板详细信息
- 权限: 所有用户
- 响应体: 同创建看板的响应

5. 获取看板任务
- 路径: `GET /api/boards/{id}/tasks`
- 功能: 获取看板中的所有任务
- 权限: 所有用户
- 查询参数:
  - columnId: 列ID（可选）
- 响应体:
```json
[
    {
        "id": "number",
        "title": "string",
        "columnId": "number",
        "assigneeId": "number",
        "priority": "string",
        "status": "string"
    }
]
```

6. 归档看板
- 路径: `POST /api/boards/{id}/archive`
- 功能: 归档看板
- 权限: 仅管理员
- 状态码:
  - 200: 归档成功
  - 400: 看板状态错误
  - 403: 无权限
  - 404: 看板不存在

### 2.3 Sprint模块

#### 2.3.1 数据模型
1. Sprint状态
```java
public enum SprintStatus {
    PLANNING,   // 规划中
    ACTIVE,     // 进行中
    COMPLETED   // 已完成
}
```

#### 2.3.2 Sprint接口
1. 创建Sprint
- 路径: `POST /api/sprints`
- 功能: 创建新的Sprint
- 权限: 仅管理员
- 请求体:
```json
{
    "name": "string",
    "startDate": "string",
    "endDate": "string"
}
```
- 响应体:
```json
{
    "id": "number",
    "name": "string",
    "startDate": "string",
    "endDate": "string",
    "status": "string",
    "createdBy": "number",
    "createdAt": "string"
}
```
- 状态码:
  - 201: 创建成功
  - 400: 请求参数错误
  - 403: 无权限

2. 开始Sprint
- 路径: `POST /api/sprints/{id}/start`
- 功能: 启动Sprint
- 权限: 仅管理员
- 状态码:
  - 200: 启动成功
  - 400: Sprint状态错误
  - 403: 无权限
  - 404: Sprint不存在

3. 获取Sprint列表
- 路径: `GET /api/sprints`
- 功能: 获取Sprint列表
- 权限: 所有用户
- 查询参数:
  - status: Sprint状态（可选）
- 响应体:
```json
[
    {
        "id": "number",
        "name": "string",
        "startDate": "string",
        "endDate": "string",
        "status": "string"
    }
]
```

4. 获取Sprint详情
- 路径: `GET /api/sprints/{id}`
- 功能: 获取Sprint详细信息
- 权限: 所有用户
- 响应体: 同创建Sprint的响应

5. 完成Sprint
- 路径: `POST /api/sprints/{id}/complete`
- 功能: 完成Sprint
- 权限: 仅管理员
- 状态码:
  - 200: 完成成功
  - 400: Sprint状态错误
  - 403: 无权限
  - 404: Sprint不存在

6. 获取Sprint任务
- 路径: `GET /api/sprints/{id}/tasks`
- 功能: 获取Sprint中的所有任务
- 权限: 所有用户
- 响应体: 同看板任务列表

7. 取消Sprint
- 路径: `POST /api/sprints/{id}/cancel`
- 功能: 取消Sprint
- 权限: 仅管理员
- 状态码:
  - 200: 取消成功
  - 400: Sprint状态错误
  - 403: 无权限
  - 404: Sprint不存在

### 2.4 任务模块

#### 2.4.1 数据模型
1. 任务状态
```java
public enum TaskStatus {
    TODO,           // 待处理
    IN_PROGRESS,    // 进行中
    DONE           // 已完成
}
```

#### 2.4.2 任务接口
1. 创建任务
- 路径: `POST /api/tasks`
- 功能: 创建新任务
- 权限: 所有用户
- 请求体:
```json
{
    "title": "string",
    "description": "string",
    "sprintId": "number",
    "columnId": "number",
    "assigneeId": "number",
    "priority": "string"
}
```
- 响应体:
```json
{
    "id": "number",
    "title": "string",
    "description": "string",
    "sprintId": "number",
    "columnId": "number",
    "assigneeId": "number",
    "reporterId": "number",
    "priority": "string",
    "status": "string",
    "createdAt": "string"
}
```
- 状态码:
  - 201: 创建成功
  - 400: 请求参数错误
  - 404: 相关资源不存在

2. 更新任务状态
- 路径: `PUT /api/tasks/{id}/status`
- 功能: 更新任务状态
- 权限: 所有用户
- 请求体:
```json
{
    "columnId": "number",
    "comment": "string"
}
```
- 状态码:
  - 200: 更新成功
  - 400: 请求参数错误
  - 404: 任务不存在
  - 409: 列的WIP限制超出

3. 获取任务列表
- 路径: `GET /api/tasks`
- 功能: 获取任务列表
- 权限: 所有用户
- 查询参数:
  - sprintId: Sprint ID（可选）
  - status: 任务状态（可选）
  - assigneeId: 经办人ID（可选）
  - page: 页码
  - size: 每页大小
- 响应体:
```json
{
    "content": [
        {
            "id": "number",
            "title": "string",
            "sprintId": "number",
            "columnId": "number",
            "assigneeId": "number",
            "priority": "string",
            "status": "string"
        }
    ],
    "totalElements": "number",
    "totalPages": "number"
}
```

4. 获取任务详情
- 路径: `GET /api/tasks/{id}`
- 功能: 获取任务详细信息
- 权限: 所有用户
- 响应体: 同创建任务的响应

5. 更新任务
- 路径: `PUT /api/tasks/{id}`
- 功能: 更新任务基本信息
- 权限: 所有用户
- 请求体:
```json
{
    "title": "string",
    "description": "string",
    "assigneeId": "number",
    "priority": "string"
}
```
- 状态码:
  - 200: 更新成功
  - 400: 请求参数错误
  - 404: 任务不存在

6. 移动任务到Sprint
- 路径: `PUT /api/tasks/{id}/sprint`
- 功能: 将任务移动到其他Sprint
- 权限: 所有用户
- 请求体:
```json
{
    "sprintId": "number"
}
```
- 状态码:
  - 200: 移动成功
  - 400: 请求参数错误
  - 404: 任务或Sprint不存在

7. 获取Backlog任务
- 路径: `GET /api/tasks/backlog`
- 功能: 获取未规划到Sprint的任务
- 权限: 所有用户
- 查询参数:
  - page: 页码
  - size: 每页大小
  - sort: 排序字段
- 响应体:
```json
{
    "content": [
        {
            "id": "number",
            "title": "string",
            "description": "string",
            "assigneeId": "number",
            "priority": "string",
            "status": "string",
            "createdAt": "string"
        }
    ],
    "totalElements": "number",
    "totalPages": "number"
}
```

## 3. 系统优化设计

### 3.1 性能优化
1. 数据库优化
   - 索引优化
   - 查询优化
   - 分页优化

2. 并发处理
   - 乐观锁控制
   - 并发更新处理

### 3.2 安全防护
- SQL注入防护
- XSS防护
- CSRF防护 