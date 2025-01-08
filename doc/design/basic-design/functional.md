# GIRA项目功能设计文档

## 1. 系统功能模块

### 1.1 核心模块
1. 认证模块（Authentication）
   - 用户登录
   - Token管理
   - 权限验证

2. 用户模块（User）
   - 用户管理
   - 角色管理
   - 个人信息管理

3. 看板模块（Board）
   - 看板配置
   - 任务展示
   - 状态管理

4. Sprint模块（Sprint）
   - Sprint管理
   - 任务分配
   - 进度跟踪

5. 任务模块（Task）
   - 任务管理
   - 状态流转
   - 优先级管理

## 2. 功能流程设计

### 2.1 用户认证流程
```mermaid
sequenceDiagram
    participant User
    participant Frontend
    participant Backend
    participant Database
    
    User->>Frontend: 输入用户名密码
    Frontend->>Backend: 发送登录请求
    Backend->>Database: 验证用户信息
    Database-->>Backend: 返回用户数据
    Backend->>Backend: 生成JWT Token
    Backend-->>Frontend: 返回Token和用户信息
    Frontend->>Frontend: 保存Token
    Frontend-->>User: 登录成功，跳转首页
```

### 2.2 看板操作流程
```mermaid
sequenceDiagram
    participant User
    participant Frontend
    participant Backend
    participant Database
    
    User->>Frontend: 访问看板页面
    Frontend->>Backend: 获取看板数据
    Backend->>Database: 查询看板配置
    Database-->>Backend: 返回看板数据
    Backend-->>Frontend: 返回看板视图数据
    Frontend-->>User: 展示看板
    
    User->>Frontend: 拖动任务卡片
    Frontend->>Backend: 更新任务状态
    Backend->>Database: 保存状态变更
    Database-->>Backend: 确认更新
    Backend-->>Frontend: 返回更新结果
    Frontend-->>User: 更新UI显示
```

### 2.3 Sprint管理流程
```mermaid
sequenceDiagram
    participant Admin
    participant Frontend
    participant Backend
    participant Database
    
    Admin->>Frontend: 创建Sprint
    Frontend->>Backend: 发送Sprint数据
    Backend->>Database: 保存Sprint信息
    Database-->>Backend: 确认创建
    Backend-->>Frontend: 返回Sprint详情
    Frontend-->>Admin: 显示创建成功
    
    Admin->>Frontend: 分配任务到Sprint
    Frontend->>Backend: 更新任务关联
    Backend->>Database: 保存任务关联
    Database-->>Backend: 确认更新
    Backend-->>Frontend: 返回更新结果
    Frontend-->>Admin: 更新任务显示
```

### 2.4 任务管理流程
```mermaid
sequenceDiagram
    participant Developer
    participant Frontend
    participant Backend
    participant Database
    
    Developer->>Frontend: 创建任务
    Frontend->>Backend: 发送任务数据
    Backend->>Database: 保存任务信息
    Database-->>Backend: 确认创建
    Backend-->>Frontend: 返回任务详情
    Frontend-->>Developer: 显示新任务
    
    Developer->>Frontend: 更新任务状态
    Frontend->>Backend: 发送状态更新
    Backend->>Database: 保存状态变更
    Database-->>Backend: 确认更新
    Backend-->>Frontend: 返回更新结果
    Frontend-->>Developer: 更新显示状态
```

## 3. 权限控制矩阵

### 3.1 功能权限
| 功能模块 | 管理员 | 开发者 |
|---------|--------|--------|
| 用户管理 | ✓ | 仅自己 |
| 看板配置 | ✓ | × |
| Sprint管理 | ✓ | × |
| 任务创建 | ✓ | ✓ |
| 任务状态更新 | ✓ | ✓ |
| 任务删除 | ✓ | × |

### 3.2 数据权限
| 数据类型 | 管理员 | 开发者 |
|---------|--------|--------|
| 用户数据 | 所有 | 仅自己 |
| 看板数据 | 读写 | 只读 |
| Sprint数据 | 读写 | 只读 |
| 任务数据 | 所有 | 读写 |

## 4. 接口设计

### 4.1 认证接口
```
POST /api/auth/login
POST /api/auth/logout
POST /api/auth/refresh-token
```

### 4.2 用户接口
```
GET /api/users
POST /api/users
PUT /api/users/{id}
GET /api/users/{id}
PUT /api/users/{id}/password
```

### 4.3 看板接口
```
GET /api/boards
POST /api/boards
PUT /api/boards/{id}
GET /api/boards/{id}
GET /api/boards/{id}/columns
PUT /api/boards/{id}/columns
```

### 4.4 Sprint接口
```
GET /api/sprints
POST /api/sprints
PUT /api/sprints/{id}
GET /api/sprints/{id}
POST /api/sprints/{id}/start
POST /api/sprints/{id}/complete
```

### 4.5 任务接口
```
GET /api/tasks
POST /api/tasks
PUT /api/tasks/{id}
GET /api/tasks/{id}
DELETE /api/tasks/{id}
PUT /api/tasks/{id}/status
PUT /api/tasks/{id}/assignee
``` 