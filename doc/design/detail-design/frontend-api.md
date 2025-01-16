# GIRA前端API接口文档

## 1. 认证接口

### 1.1 登录
- 请求路径：`/api/auth/login`
- 请求方法：POST
- 请求参数：
```typescript
interface LoginRequest {
  username: string;  // 用户名
  password: string;  // 密码
}
```
- 响应数据：
```typescript
interface LoginResponse {
  accessToken: string;  // 访问令牌
  tokenType: string;    // 令牌类型，固定为"Bearer"
  expiresIn: number;    // 过期时间（秒）
}
```

### 1.2 登出
- 请求路径：`/api/auth/logout`
- 请求方法：POST
- 响应数据：无

## 2. 看板接口

### 2.1 获取所有看板
- 请求路径：`/api/boards`
- 请求方法：GET
- 请求参数：
```typescript
interface GetBoardsParams {
  status?: 'ACTIVE' | 'ARCHIVED';
  page?: number;
  size?: number;
  sort?: string;
}
```
- 响应数据：`PageResponse<BoardResponse>`

### 2.2 获取当前看板
- 请求路径：`/api/boards/active`
- 请求方法：GET
- 响应数据：
```typescript
interface BoardResponse {
  id: number;
  name: string;
  description: string;
  status: 'ACTIVE' | 'ARCHIVED';
  createdBy: UserResponse;
  createdAt: string;
}
```

### 2.3 更新看板信息
- 请求路径：`/api/boards/{id}`
- 请求方法：PUT
- 权限要求：ADMIN
- 请求参数：
```typescript
interface UpdateBoardRequest {
  name: string;
  description: string;
}
```
- 响应数据：`BoardResponse`

## 3. Sprint接口

### 3.1 获取Sprint列表
- 请求路径：`/api/sprints`
- 请求方法：GET
- 请求参数：
```typescript
interface GetSprintsParams {
  status?: 'PLANNING' | 'ACTIVE' | 'COMPLETED';
  page?: number;
  size?: number;
  sort?: string;
}
```
- 响应数据：
```typescript
interface PageResponse<SprintResponse> {
  content: SprintResponse[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

interface SprintResponse {
  id: number;
  name: string;
  startDate: string;
  endDate: string;
  status: 'PLANNING' | 'ACTIVE' | 'COMPLETED';
  createdAt: string;
}
```

### 3.2 获取Sprint详情
- 请求路径：`/api/sprints/{id}`
- 请求方法：GET
- 响应数据：`SprintResponse`

### 3.3 创建Sprint
- 请求路径：`/api/sprints`
- 请求方法：POST
- 权限要求：ADMIN
- 请求参数：
```typescript
interface CreateSprintRequest {
  name: string;
  startDate: string;
  endDate: string;
}
```
- 响应数据：`SprintResponse`

### 3.4 更新Sprint
- 请求路径：`/api/sprints/{id}`
- 请求方法：PUT
- 权限要求：ADMIN
- 请求参数：
```typescript
interface UpdateSprintRequest {
  name: string;
  startDate: string;
  endDate: string;
}
```
- 响应数据：`SprintResponse`

### 3.5 开始Sprint
- 请求路径：`/api/sprints/{id}/start`
- 请求方法：PUT
- 权限要求：ADMIN
- 响应数据：`SprintResponse`

### 3.6 完成Sprint
- 请求路径：`/api/sprints/{id}/complete`
- 请求方法：PUT
- 权限要求：ADMIN
- 响应数据：`SprintResponse`

### 3.7 获取Sprint任务列表
- 请求路径：`/api/sprints/{id}/tasks`
- 请求方法：GET
- 响应数据：`TaskResponse[]`

## 4. 任务接口

### 4.1 创建任务
- 请求路径：`/api/tasks`
- 请求方法：POST
- 请求参数：
```typescript
interface CreateTaskRequest {
  title: string;
  description?: string;
  sprintId: number;
  assigneeId?: number;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
}
```
- 响应数据：`TaskResponse`

### 4.2 更新任务状态
- 请求路径：`/api/tasks/{id}/status`
- 请求方法：PUT
- 请求参数：
```typescript
interface UpdateTaskStatusRequest {
  status: 'TODO' | 'IN_PROGRESS' | 'DONE';
}
```
- 响应数据：`TaskResponse`

### 4.3 获取任务详情
- 请求路径：`/api/tasks/{id}`
- 请求方法：GET
- 响应数据：
```typescript
interface TaskResponse {
  id: number;
  title: string;
  description?: string;
  status: 'TODO' | 'IN_PROGRESS' | 'DONE';
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
  sprintId: number;
  sprintName: string;
  assignee?: UserResponse;
  reporter: UserResponse;
  createdAt: string;
}
```

### 4.4 更新任务
- 请求路径：`/api/tasks/{id}`
- 请求方法：PUT
- 请求参数：
```typescript
interface UpdateTaskRequest {
  title: string;
  description?: string;
  assigneeId?: number;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
}
```
- 响应数据：`TaskResponse`

### 4.5 删除任务
- 请求路径：`/api/tasks/{id}`
- 请求方法：DELETE
- 权限要求：ADMIN

## 5. 用户接口

### 5.1 获取当前用户信息
- 请求路径：`/api/users/current`
- 请求方法：GET
- 响应数据：
```typescript
interface UserResponse {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: 'ADMIN' | 'DEVELOPER';
  status: 'ACTIVE' | 'INACTIVE';
}
```

### 5.2 获取用户列表
- 请求路径：`/api/users`
- 请求方法：GET
- 权限要求：ADMIN
- 请求参数：
```typescript
interface GetUsersParams {
  page?: number;
  size?: number;
}
```
- 响应数据：`PageResponse<UserResponse>`

### 5.3 创建用户
- 请求路径：`/api/users`
- 请求方法：POST
- 权限要求：ADMIN
- 请求参数：
```typescript
interface CreateUserRequest {
  username: string;
  password: string;
  email: string;
  fullName: string;
  role: 'ADMIN' | 'DEVELOPER';
}
```
- 响应数据：`UserResponse`

### 5.4 更新用户信息
- 请求路径：`/api/users/{id}`
- 请求方法：PUT
- 请求参数：
```typescript
interface UpdateUserRequest {
  email: string;
  fullName: string;
}
```
- 响应数据：`UserResponse`

### 5.5 删除用户
- 请求路径：`/api/users/{id}`
- 请求方法：DELETE
- 权限要求：ADMIN

### 5.6 修改密码
- 请求路径：`/api/users/{id}/password`
- 请求方法：PUT
- 请求参数：
```typescript
interface UpdatePasswordRequest {
  oldPassword: string;  // 旧密码
  newPassword: string;  // 新密码
}
```
- 响应数据：无 