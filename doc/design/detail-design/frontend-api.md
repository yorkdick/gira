# GIRA项目前端API接口文档

## 1. 认证接口

### 1.1 用户登录
- 路径: `/api/auth/login`
- 方法: `POST`
- 入参:
```typescript
interface LoginRequest {
  username: string;
  password: string;
}
```
- 返回值:
```typescript
interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: {
    id: number;
    username: string;
    email: string;
    fullName: string;
    role: 'ADMIN' | 'DEVELOPER';
  }
}
```

### 1.2 用户登出
- 路径: `/api/auth/logout`
- 方法: `POST`
- 入参: 无
- 返回值: 
```typescript
interface LogoutResponse {
  success: boolean;
}
```

### 1.3 刷新Token
- 路径: `/api/auth/refresh-token`
- 方法: `POST`
- 入参:
```typescript
interface RefreshTokenRequest {
  refreshToken: string;
}
```
- 返回值:
```typescript
interface RefreshTokenResponse {
  accessToken: string;
  refreshToken: string;
}
```

## 2. 用户接口

### 2.1 创建用户
- 路径: `/api/users`
- 方法: `POST`
- 入参:
```typescript
interface CreateUserRequest {
  username: string;
  password: string;
  email: string;
  fullName: string;
  role: 'ADMIN' | 'DEVELOPER';
}
```
- 返回值:
```typescript
interface UserResponse {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: 'ADMIN' | 'DEVELOPER';
  status: 'ACTIVE' | 'INACTIVE';
  createdAt: string;
  updatedAt: string;
}
```

### 2.2 更新用户
- 路径: `/api/users/{id}`
- 方法: `PUT`
- 入参:
```typescript
interface UpdateUserRequest {
  email?: string;
  fullName?: string;
  status?: 'ACTIVE' | 'INACTIVE';
}
```
- 返回值: `UserResponse`

### 2.3 修改密码
- 路径: `/api/users/{id}/password`
- 方法: `PUT`
- 入参:
```typescript
interface UpdatePasswordRequest {
  oldPassword: string;
  newPassword: string;
}
```
- 返回值:
```typescript
interface UpdatePasswordResponse {
  success: boolean;
}
```

### 2.4 获取用户详情
- 路径: `/api/users/{id}`
- 方法: `GET`
- 入参: 无
- 返回值: `UserResponse`

### 2.5 获取用户列表
- 路径: `/api/users`
- 方法: `GET`
- 入参:
```typescript
interface GetUsersRequest {
  page?: number;
  size?: number;
  role?: 'ADMIN' | 'DEVELOPER';
  status?: 'ACTIVE' | 'INACTIVE';
}
```
- 返回值:
```typescript
interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

type GetUsersResponse = PageResponse<UserResponse>;
```

### 2.6 获取当前用户信息
- 路径: `/api/users/current`
- 方法: `GET`
- 入参: 无
- 返回值: `UserResponse`

## 3. 看板接口

### 3.1 创建看板
- 路径: `/api/boards`
- 方法: `POST`
- 入参:
```typescript
interface CreateBoardRequest {
  name: string;
  description?: string;
  columns: {
    name: string;
    orderIndex: number;
  }[];
}
```
- 返回值:
```typescript
interface BoardResponse {
  id: number;
  name: string;
  description?: string;
  status: 'ACTIVE' | 'ARCHIVED';
  columns: {
    id: number;
    name: string;
    orderIndex: number;
  }[];
  createdBy: number;
  createdAt: string;
  updatedAt: string;
}
```

### 3.2 更新看板
- 路径: `/api/boards/{id}`
- 方法: `PUT`
- 入参:
```typescript
interface UpdateBoardRequest {
  name?: string;
  description?: string;
  status?: 'ACTIVE' | 'ARCHIVED';
}
```
- 返回值: `BoardResponse`

### 3.3 更新看板列
- 路径: `/api/boards/{id}/columns`
- 方法: `PUT`
- 入参:
```typescript
interface UpdateBoardColumnsRequest {
  columns: {
    id: number;
    name: string;
    orderIndex: number;
  }[];
}
```
- 返回值: `BoardResponse`

### 3.4 获取看板详情
- 路径: `/api/boards/{id}`
- 方法: `GET`
- 入参: 无
- 返回值: `BoardResponse`

### 3.5 获取看板列表
- 路径: `/api/boards`
- 方法: `GET`
- 入参:
```typescript
interface GetBoardsRequest {
  page?: number;
  size?: number;
  status?: 'ACTIVE' | 'ARCHIVED' = 'ACTIVE';
}
```
- 返回值: `PageResponse<BoardResponse>`

### 3.6 归档看板
- 路径: `/api/boards/{id}/archive`
- 方法: `PUT`
- 入参: 无
- 返回值: void

## 4. Sprint接口

### 4.1 创建Sprint
- 路径: `/api/sprints`
- 方法: `POST`
- 入参:
```typescript
interface CreateSprintRequest {
  boardId: number;
  name: string;
  startDate: string;
  endDate: string;
}
```
- 返回值:
```typescript
interface SprintResponse {
  id: number;
  boardId: number;
  name: string;
  startDate?: string;
  endDate?: string;
  status: 'PLANNING' | 'ACTIVE' | 'COMPLETED';
  createdBy: number;
  createdAt: string;
  updatedAt: string;
}
```

### 4.2 更新Sprint
- 路径: `/api/sprints/{id}`
- 方法: `PUT`
- 入参:
```typescript
interface UpdateSprintRequest {
  name?: string;
  startDate?: string;
  endDate?: string;
}
```
- 返回值: `SprintResponse`

### 4.3 开始Sprint
- 路径: `/api/sprints/{id}/start`
- 方法: `PUT`
- 入参: 无
- 返回值: `SprintResponse`

### 4.4 完成Sprint
- 路径: `/api/sprints/{id}/complete`
- 方法: `PUT`
- 入参: 无
- 返回值: `SprintResponse`

### 4.5 获取Sprint详情
- 路径: `/api/sprints/{id}`
- 方法: `GET`
- 入参: 无
- 返回值: `SprintResponse`

### 4.6 获取看板的Sprint列表
- 路径: `/api/sprints/boards/{boardId}/sprints`
- 方法: `GET`
- 入参:
```typescript
interface GetSprintsRequest {
  page?: number;
  size?: number;
  status?: 'PLANNING' | 'ACTIVE' | 'COMPLETED';
}
```
- 返回值: `PageResponse<SprintResponse>`

### 4.7 获取Sprint任务
- 路径: `/api/sprints/{id}/tasks`
- 方法: `GET`
- 入参: 无
- 返回值: `TaskResponse[]`

## 5. 任务接口

### 5.1 创建任务
- 路径: `/api/tasks`
- 方法: `POST`
- 入参:
```typescript
interface CreateTaskRequest {
  title: string;
  description?: string;
  boardId: number;
  sprintId?: number;
  columnId: number;
  assigneeId?: number;
  priority?: 'HIGH' | 'MEDIUM' | 'LOW';
}
```
- 返回值:
```typescript
interface TaskResponse {
  id: number;
  title: string;
  description?: string;
  boardId: number;
  sprintId?: number;
  columnId: number;
  assigneeId?: number;
  reporterId: number;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
  status: 'TODO' | 'IN_PROGRESS' | 'DONE';
  createdAt: string;
  updatedAt: string;
}
```

### 5.2 更新任务
- 路径: `/api/tasks/{id}`
- 方法: `PUT`
- 入参:
```typescript
interface UpdateTaskRequest {
  title?: string;
  description?: string;
  sprintId?: number;
  columnId?: number;
  assigneeId?: number;
  priority?: 'HIGH' | 'MEDIUM' | 'LOW';
}
```
- 返回值: `TaskResponse`

### 5.3 更新任务状态
- 路径: `/api/tasks/{id}/status`
- 方法: `PUT`
- 入参:
```typescript
interface UpdateTaskStatusRequest {
  status: 'TODO' | 'IN_PROGRESS' | 'DONE';
}
```
- 返回值: `TaskResponse`

### 5.4 获取任务详情
- 路径: `/api/tasks/{id}`
- 方法: `GET`
- 入参: 无
- 返回值: `TaskResponse`

### 5.5 获取任务列表
- 路径: `/api/tasks`
- 方法: `GET`
- 入参:
```typescript
interface GetTasksRequest {
  page?: number;
  size?: number;
  boardId?: number;
  sprintId?: number;
  columnId?: number;
  assigneeId?: number;
  status?: 'TODO' | 'IN_PROGRESS' | 'DONE';
  priority?: 'HIGH' | 'MEDIUM' | 'LOW';
}
```
- 返回值: `PageResponse<TaskResponse>`

### 5.6 获取待办任务
- 路径: `/api/tasks/backlog`
- 方法: `GET`
- 入参:
```typescript
interface GetBacklogTasksRequest {
  page?: number;
  size?: number;
  boardId: number;
}
```
- 返回值: `PageResponse<TaskResponse>`

### 5.7 移动任务到Sprint
- 路径: `/api/tasks/{id}/sprint`
- 方法: `PUT`
- 入参: sprintId作为查询参数
- 返回值: `TaskResponse`

### 5.8 获取看板任务
- 路径: `/api/tasks/boards/{boardId}`
- 方法: `GET`
- 入参:
```typescript
interface GetBoardTasksRequest {
  columnId?: number;
  sprintId?: number;
}
```
- 返回值:
```typescript
interface BoardTasksResponse {
  columns: {
    id: number;
    name: string;
    tasks: TaskResponse[];
  }[];
}
```

### 5.9 获取指派的任务
- 路径: `/api/tasks/assignee/{assigneeId}`
- 方法: `GET`
- 入参:
```typescript
interface GetAssigneeTasksRequest {
  page?: number;
  size?: number;
  status?: 'TODO' | 'IN_PROGRESS' | 'DONE';
}
```
- 返回值: `PageResponse<TaskResponse>`

### 5.10 删除任务
- 路径: `/api/tasks/{id}`
- 方法: `DELETE`
- 入参: 无
- 返回值:
```typescript
interface DeleteTaskResponse {
  success: boolean;
}
``` 