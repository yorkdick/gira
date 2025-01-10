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

// 用户只能访问自己的资源
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
```

#### 2.1.3 认证接口
1. 用户登录
- 路径: `POST /api/auth/login`
- 功能: 用户登录并获取访问令牌
- 权限: 无需权限
- 业务逻辑: 验证用户名密码，生成JWT令牌
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
- 错误码：
  - 400: 请求参数错误
  - 401: 认证失败

2. 用户登出
- 路径: `POST /api/auth/logout`
- 功能: 用户登出系统
- 权限: 已登录用户
- 业务逻辑: 清除用户会话信息
- 响应: 200 OK
- 错误码：
  - 401: 未登录或Token无效

3. 刷新令牌
- 路径: `POST /api/auth/refresh-token`
- 功能: 使用刷新令牌获取新的访问令牌
- 权限: 无需权限
- 业务逻辑: 验证刷新令牌有效性，生成新的访问令牌
- 请求体:
```json
{
    "refreshToken": "string"
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
- 错误码：
  - 400: 请求参数错误
  - 401: Token无效

#### 2.1.4 用户管理接口
1. 创建用户
- 路径: `POST /api/users`
- 功能: 创建新用户
- 权限: 仅管理员
- 业务逻辑: 验证用户信息，创建新用户账号
- 请求体:
```json
{
    "username": "string",
    "email": "string",
    "fullName": "string",
    "password": "string",
    "role": "DEVELOPER"  // 只能创建DEVELOPER角色，ADMIN角色为系统预置
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
- 错误码：
  - 400: 请求参数错误
  - 403: 无权限
  - 409: "用户名已存在"或"邮箱已存在"

2. 更新用户信息
- 路径: `PUT /api/users/{id}`
- 功能: 更新用户基本信息
- 权限: 用户本人或管理员
- 业务逻辑: 验证并更新用户信息
- 请求体:
```json
{
    "email": "string",
    "fullName": "string"
}
```
- 错误码：
  - 400: 请求参数错误
  - 403: "无权限修改其他用户信息"
  - 404: "用户不存在"
  - 409: "邮箱已存在"

3. 修改密码
- 路径: `PUT /api/users/{id}/password`
- 功能: 修改用户密码
- 权限: 用户本人或管理员
- 业务逻辑: 验证旧密码，更新新密码
- 请求体:
```json
{
    "oldPassword": "string",
    "newPassword": "string"
}
```
- 错误码：
  - 400: "原密码错误"
  - 403: "无权限修改其他用户信息"
  - 404: "用户不存在"

4. 获取用户信息
- 路径: `GET /api/users/{id}`
- 功能: 获取指定用户信息
- 权限: 所有已登录用户
- 业务逻辑: 返回用户基本信息
- 错误码：
  - 404: 用户不存在

5. 获取用户列表
- 路径: `GET /api/users`
- 功能: 获取用户列表
- 权限: 仅管理员
- 业务逻辑: 分页返回用户列表
- 查询参数:
  - page: 页码（从0开始）
  - size: 每页大小
  - sort: 排序字段
- 错误码：
  - 403: 无权限

6. 删除用户
- 路径: `DELETE /api/users/{id}`
- 功能: 删除用户
- 权限: 仅管理员
- 业务逻辑: 删除指定用户账号
- 错误码：
  - 403: 无权限
  - 404: 用户不存在
  - 409: 用户有关联数据，无法删除

### 2.2 看板模块

#### 2.2.1 数据模型
1. 看板状态
```java
public enum BoardStatus {
    ACTIVE,     // 活动状态
    ARCHIVED    // 已归档
}
```

#### 2.2.2 看板接口
1. 创建看板
- 路径: `POST /api/boards`
- 功能: 创建新的看板
- 权限: 仅管理员
- 业务逻辑: 创建看板及其列配置
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
    "token": "string",
    "tokenType": "Bearer",
    "expiresIn": "number"
}
```
- 错误码：
  - 400: 请求参数错误
  - 403: 无权限

2. 更新看板
- 路径: `PUT /api/boards/{id}`
- 功能: 更新看板基本信息
- 权限: 管理员或经理
- 业务逻辑: 更新看板的名称和描述
- 请求体:
```json
{
    "name": "string",
    "description": "string"
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
- 错误码：
  - 400: 请求参数错误
  - 403: 无权限
  - 404: 看板不存在

3. 更新看板列
- 路径: `PUT /api/boards/{id}/columns`
- 功能: 更新看板列配置
- 权限: 管理员或经理
- 业务逻辑: 更新看板的列配置，包括列名、顺序和在制品限制
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
- 响应体:
```json
{
    "token": "string",
    "tokenType": "Bearer",
    "expiresIn": "number"
}
```
- 错误码：
  - 400: "无法删除包含任务的列"
  - 403: 无权限
  - 404: "看板列不存在"

4. 获取看板
- 路径: `GET /api/boards/{id}`
- 功能: 获取看板详细信息
- 权限: 所有已登录用户
- 业务逻辑: 返回看板及其列的详细信息
- 错误码：
  - 404: 看板不存在

5. 获取看板列表
- 路径: `GET /api/boards`
- 功能: 获取看板列表
- 权限: 所有已登录用户
- 业务逻辑: 分页返回看板列表，可按状态筛选
- 查询参数:
  - status: 看板状态（ACTIVE/ARCHIVED）
  - page: 页码
  - size: 每页大小
  - sort: 排序字段
- 错误码：
  - 400: 请求参数错误

6. 归档看板
- 路径: `PUT /api/boards/{id}/archive`
- 功能: 归档看板
- 权限: 仅管理员
- 业务逻辑: 将看板状态更改为已归档
- 错误码：
  - 403: 无权限
  - 404: 看板不存在
  - 409: 看板已归档

7. 获取看板任务
- 路径: `GET /api/boards/{id}/tasks`
- 功能: 获取看板中的任务
- 权限: 所有已登录用户
- 业务逻辑: 返回看板中的所有任务，可按列筛选
- 查询参数:
  - columnId: 看板列ID（可选）
- 错误码：
  - 404: 看板不存在

### 2.3 Sprint模块

#### 2.3.1 数据模型
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
- 业务逻辑: 创建新的Sprint，初始状态为规划中
- 请求体:
```json
{
    "name": "string",
    "startDate": "string",
    "endDate": "string",
    "boardId": "number",
    "goal": "string"
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
- 错误码：
  - 400: "开始日期不能晚于结束日期"或"开始日期不能早于今天"
  - 403: 无权限
  - 409: 日期与其他Sprint冲突

2. 更新Sprint
- 路径: `PUT /api/sprints/{id}`
- 功能: 更新Sprint信息
- 权限: 仅管理员
- 业务逻辑: 更新Sprint的基本信息，不包括状态变更
- 请求体:
```json
{
    "name": "string",
    "startDate": "string",
    "endDate": "string",
    "goal": "string"
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
- 错误码：
  - 400: "开始日期不能晚于结束日期"或"开始日期不能早于今天"
  - 403: 无权限
  - 404: "Sprint不存在"
  - 409: 日期与其他Sprint冲突

3. 获取Sprint
- 路径: `GET /api/sprints/{id}`
- 功能: 获取Sprint详细信息
- 权限: 所有已登录用户
- 业务逻辑: 返回Sprint的详细信息，包括进度统计
- 错误码：
  - 404: Sprint不存在

4. 开始Sprint
- 路径: `PUT /api/sprints/{id}/start`
- 功能: 将Sprint状态更改为进行中
- 权限: 仅管理员
- 业务逻辑: 检查Sprint是否可以开始，更新状态
- 错误码：
  - 400: "只能启动计划中的Sprint"或"已存在活动中的Sprint"
  - 403: 无权限
  - 404: "Sprint不存在"

5. 完成Sprint
- 路径: `PUT /api/sprints/{id}/complete`
- 功能: 将Sprint状态更改为已完成
- 权限: 仅管理员
- 业务逻辑: 检查Sprint是否可以完成，更新状态
- 错误码：
  - 400: "只能完成活动中的Sprint"或"Sprint中还有未完成的任务"
  - 403: 无权限
  - 404: "Sprint不存在"

6. 取消Sprint
- 路径: `PUT /api/sprints/{id}/cancel`
- 功能: 取消Sprint
- 权限: 仅管理员
- 业务逻辑: 将任务移回待办列表，删除Sprint
- 错误码：
  - 404: "Sprint不存在"

7. 获取Sprint列表
- 路径: `GET /api/sprints`
- 功能: 获取Sprint列表
- 权限: 所有已登录用户
- 业务逻辑: 分页返回Sprint列表，开发者只能看到自己参与的Sprint
- 查询参数:
  - status: Sprint状态（可选）
  - page: 页码
  - size: 每页大小
  - sort: 排序字段（默认按创建时间降序）
- 错误码：
  - 400: 请求参数错误

8. 获取Sprint详情
- 路径: `GET /api/sprints/{id}`
- 功能: 获取Sprint详细信息
- 权限: 所有已登录用户
- 业务逻辑: 返回Sprint详细信息，包括任务列表（开发者只能看到自己的任务）
- 错误码：
  - 404: "Sprint不存在"

9. 获取看板的Sprint列表
- 路径: `GET /api/sprints/boards/{boardId}/sprints`
- 功能: 获取指定看板的Sprint列表
- 权限: 所有已登录用户
- 业务逻辑: 分页返回指定看板的Sprint列表
- 查询参数:
  - page: 页码
  - size: 每页大小
  - sort: 排序字段（默认按创建时间降序）
- 错误码：
  - 404: 看板不存在

10. 获取Sprint任务
- 路径: `GET /api/sprints/{id}/tasks`
- 功能: 获取Sprint中的任务
- 权限: 所有已登录用户
- 业务逻辑: 返回Sprint中的所有任务
- 错误码：
  - 404: Sprint不存在

### 2.4 任务模块

#### 2.4.1 数据模型
```java
public enum TaskStatus {
    TODO,       // 待办
    IN_PROGRESS,// 进行中
    DONE        // 已完成
}

public enum TaskPriority {
    LOW,        // 低优先级
    MEDIUM,     // 中优先级
    HIGH        // 高优先级
}
```

#### 2.4.2 任务接口
1. 创建任务
- 路径: `POST /api/tasks`
- 功能: 创建新任务
- 权限: 所有已登录用户
- 业务逻辑: 创建任务，检查看板列的在制品限制
- 请求体:
```json
{
    "title": "string",
    "description": "string",
    "boardId": "number",
    "columnId": "number",
    "assigneeId": "number",
    "priority": "string",
    "sprintId": "number"
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
- 错误码：
  - 400: "看板不存在"或"看板列不属于指定的看板"
  - 404: "看板列不存在"或"Sprint不存在"或"指派人不存在"
  - 409: "看板列任务数量已达到限制"

2. 更新任务
- 路径: `PUT /api/tasks/{id}`
- 功能: 更新任务信息
- 权限: 所有已登录用户
- 业务逻辑: 更新任务基本信息，检查看板列的在制品限制
- 请求体:
```json
{
    "title": "string",
    "description": "string",
    "assigneeId": "number",
    "priority": "string"
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
- 错误码：
  - 400: "无效的状态变更"
  - 404: "任务不存在"或"看板列不存在"或"指派人不存在"
  - 409: "看板列任务数量已达到限制"

3. 更新任务状态
- 路径: `PUT /api/tasks/{id}/status`
- 功能: 更新任务状态
- 权限: 所有已登录用户
- 业务逻辑: 更新任务状态，检查状态转换规则和看板列的在制品限制
- 请求体:
```json
{
    "status": "string",
    "columnId": "number"
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
- 错误码：
  - 404: "任务不存在"或"看板列不存在"
  - 409: "看板列任务数量已达到限制"

4. 获取任务
- 路径: `GET /api/tasks/{id}`
- 功能: 获取任务详细信息
- 权限: 所有已登录用户
- 业务逻辑: 返回任务的详细信息
- 错误码：
  - 404: 任务不存在

5. 获取任务列表
- 路径: `GET /api/tasks`
- 功能: 获取任务列表
- 权限: 所有已登录用户
- 业务逻辑: 分页返回任务列表，支持多种筛选条件
- 查询参数:
  - sprintId: Sprint ID（可选）
  - assigneeId: 经办人ID（可选）
  - page: 页码
  - size: 每页大小
  - sort: 排序字段
- 错误码：
  - 400: 请求参数错误

6. 获取待办任务
- 路径: `GET /api/tasks/backlog`
- 功能: 获取待办任务列表
- 权限: 所有已登录用户
- 业务逻辑: 返回未分配到Sprint的任务列表
- 错误码：
  - 400: 请求参数错误

7. 移动任务到Sprint
- 路径: `PUT /api/tasks/{id}/sprint`
- 功能: 将任务移动到Sprint或从Sprint中移除
- 权限: 所有已登录用户
- 业务逻辑: 更新任务的Sprint关联
- 查询参数:
  - sprintId: Sprint ID（可选，不提供则移出Sprint）
- 错误码：
  - 404: 任务或Sprint不存在

8. 获取看板任务
- 路径: `GET /api/tasks/boards/{boardId}`
- 功能: 获取看板中的任务
- 权限: 所有已登录用户
- 业务逻辑: 分页返回指定看板的任务列表
- 错误码：
  - 404: 看板不存在

9. 获取经办人任务
- 路径: `GET /api/tasks/assignee/{assigneeId}`
- 功能: 获取指定经办人的任务
- 权限: 所有已登录用户
- 业务逻辑: 分页返回指定经办人的任务列表
- 错误码：
  - 404: 经办人不存在

10. 删除任务
- 路径: `DELETE /api/tasks/{id}`
- 功能: 删除任务
- 权限: 仅管理员
- 业务逻辑: 删除指定任务
- 错误码：
  - 403: 无权限
  - 404: 任务不存在

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