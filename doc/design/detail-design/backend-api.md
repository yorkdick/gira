# GIRA后台功能详细设计文档

## 1. 认证模块（Authentication）

### 1.1 用户登录
- 路径: `POST /api/auth/login`
- 功能: 用户登录并获取访问令牌
- 请求体:
```json
{
    "username": "string",
    "password": "string",
    "rememberMe": "boolean"
}
```
- 响应体:
```json
{
    "token": "string",
    "refreshToken": "string",
    "tokenType": "Bearer",
    "expiresIn": "number"
}
```
- 状态码:
  - 200: 登录成功
  - 401: 用户名或密码错误
  - 403: 账户已被禁用
- 业务逻辑:
  1. 验证用户名和密码
  2. 检查用户状态是否为激活状态
  3. 生成JWT令牌，包含用户ID、角色等信息
  4. 如果rememberMe为true，生成刷新令牌
  5. 记录登录日志

### 1.2 退出登录
- 路径: `POST /api/auth/logout`
- 功能: 用户退出登录
- 请求头: `Authorization: Bearer {token}`
- 响应体: 无
- 状态码:
  - 200: 退出成功
  - 401: 未授权
- 业务逻辑:
  1. 验证当前token是否有效
  2. 将token加入黑名单
  3. 清除用户会话信息

### 1.3 刷新令牌
- 路径: `POST /api/auth/refresh-token`
- 功能: 使用刷新令牌获取新的访问令牌
- 请求体:
```json
{
    "refreshToken": "string"
}
```
- 响应体: 同登录接口
- 状态码:
  - 200: 刷新成功
  - 401: 刷新令牌无效
- 业务逻辑:
  1. 验证刷新令牌的有效性
  2. 生成新的访问令牌
  3. 如果配置了令牌轮换，同时生成新的刷新令牌

## 2. 用户模块（User）

### 2.1 创建用户
- 路径: `POST /api/users`
- 功能: 管理员创建新用户
- 权限: 仅管理员
- 请求体:
```json
{
    "username": "string",
    "email": "string",
    "fullName": "string",
    "password": "string",
    "role": "DEVELOPER"
}
```
- 响应体:
```json
{
    "id": "number",
    "username": "string",
    "email": "string",
    "fullName": "string",
    "role": "string",
    "status": "string",
    "createdAt": "string"
}
```
- 状态码:
  - 201: 创建成功
  - 400: 请求参数错误
  - 403: 无权限
  - 409: 用户名或邮箱已存在
- 业务逻辑:
  1. 验证请求者是否为管理员
  2. 验证用户名和邮箱是否已存在
  3. 密码加密存储
  4. 创建用户记录
  5. 发送欢迎邮件

### 2.2 更新用户信息
- 路径: `PUT /api/users/{id}`
- 功能: 更新用户基本信息
- 权限: 管理员或用户本人
- 请求体:
```json
{
    "email": "string",
    "fullName": "string",
    "status": "string"  // 仅管理员可设置
}
```
- 响应体: 同创建用户
- 状态码:
  - 200: 更新成功
  - 400: 请求参数错误
  - 403: 无权限
  - 404: 用户不存在
- 业务逻辑:
  1. 验证操作权限
  2. 验证邮箱唯一性
  3. 更新用户信息
  4. 如果状态发生变化，记录状态变更历史

## 3. 看板模块（Board）

### 3.1 创建看板
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
- 业务逻辑:
  1. 验证管理员权限
  2. 创建看板记录
  3. 创建看板列记录
  4. 初始化默认配置

### 3.2 更新看板列
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
- 响应体: 同创建看板
- 状态码:
  - 200: 更新成功
  - 400: 请求参数错误
  - 403: 无权限
  - 404: 看板不存在
- 业务逻辑:
  1. 验证管理员权限
  2. 验证看板是否存在
  3. 更新列配置
  4. 重新排序任务

## 4. Sprint模块（Sprint）

### 4.1 创建Sprint
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
- 业务逻辑:
  1. 验证管理员权限
  2. 验证日期有效性
  3. 创建Sprint记录
  4. 初始化Sprint状态为PLANNING

### 4.2 开始Sprint
- 路径: `POST /api/sprints/{id}/start`
- 功能: 启动Sprint
- 权限: 仅管理员
- 请求体: 无
- 响应体: 同创建Sprint
- 状态码:
  - 200: 启动成功
  - 400: Sprint状态错误
  - 403: 无权限
  - 404: Sprint不存在
- 业务逻辑:
  1. 验证管理员权限
  2. 检查Sprint状态是否为PLANNING
  3. 更新Sprint状态为ACTIVE
  4. 记录Sprint开始时间
  5. 创建Sprint开始事件

## 5. 任务模块（Task）

### 5.1 创建任务
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
- 业务逻辑:
  1. 验证必填字段
  2. 验证Sprint和列的存在性
  3. 验证经办人的有效性
  4. 创建任务记录
  5. 记录任务创建历史
  6. 如果设置了经办人，发送通知

### 5.2 更新任务状态
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
- 响应体: 同创建任务
- 状态码:
  - 200: 更新成功
  - 400: 请求参数错误
  - 404: 任务不存在
  - 409: 列的WIP限制超出
- 业务逻辑:
  1. 验证任务是否存在
  2. 检查目标列的WIP限制
  3. 更新任务状态
  4. 记录状态变更历史
  5. 如果有评论，创建评论记录
  6. 通知相关人员

## 6. 通用规范

### 6.1 错误响应格式
```json
{
    "timestamp": "string",
    "status": "number",
    "error": "string",
    "message": "string",
    "path": "string"
}
```

### 6.2 分页请求参数
- page: 页码，从0开始
- size: 每页大小
- sort: 排序字段，格式为"field,direction"

### 6.3 分页响应格式
```json
{
    "content": [],
    "totalElements": "number",
    "totalPages": "number",
    "size": "number",
    "number": "number"
}
```

### 6.4 通用状态码
- 200: 操作成功
- 201: 创建成功
- 400: 请求参数错误
- 401: 未授权
- 403: 无权限
- 404: 资源不存在
- 409: 资源冲突
- 500: 服务器错误 