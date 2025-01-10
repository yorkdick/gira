# GIRA前端开发指导文档

## 1. 项目初始化

### 1.1 环境准备
```bash
# Node.js版本要求
node >= 18.0.0
npm >= 8.0.0

# 推荐使用pnpm
npm install -g pnpm
```

### 1.2 项目创建
```bash
# 使用Vite创建项目
pnpm create vite gira-frontend --template react-ts

# 安装依赖
cd gira-frontend
pnpm install

# 安装必要依赖
pnpm add @ant-design/icons @reduxjs/toolkit react-redux react-router-dom axios antd less
pnpm add -D @types/node typescript @typescript-eslint/parser @typescript-eslint/eslint-plugin prettier eslint-config-prettier eslint-plugin-prettier
```

### 1.3 项目配置

#### vite.config.ts
```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  css: {
    preprocessorOptions: {
      less: {
        javascriptEnabled: true,
      },
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
```

#### tsconfig.json
```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "react-jsx",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  },
  "include": ["src"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

## 2. 项目结构实现

### 2.1 目录结构创建
```bash
src/
├── assets/          # 静态资源
│   ├── images/      # 图片资源
│   └── styles/      # 全局样式
├── components/      # 通用组件
│   ├── Layout/      # 布局组件
│   ├── Task/        # 任务相关组件
│   └── Board/       # 看板相关组件
├── config/          # 配置文件
│   ├── routes.ts    # 路由配置
│   └── constants.ts # 常量定义
├── hooks/           # 自定义Hooks
│   ├── useAuth.ts   # 认证相关
│   └── useRequest.ts# 请求相关
├── pages/          # 页面组件
│   ├── Login/      # 登录页面
│   ├── Board/      # 看板页面
│   └── Backlog/    # Backlog页面
├── services/       # API服务
│   ├── auth.ts     # 认证相关
│   ├── board.ts    # 看板相关
│   └── task.ts     # 任务相关
├── store/          # 状态管理
│   ├── slices/     # Redux切片
│   └── index.ts    # Store配置
├── types/          # 类型定义
│   ├── api.ts      # API类型
│   └── models.ts   # 数据模型
└── utils/          # 工具函数
    ├── request.ts  # Axios配置
    └── storage.ts  # 本地存储
```

### 2.2 核心功能实现

#### 2.2.1 网络请求封装 (utils/request.ts)
```typescript
import axios from 'axios';
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';

const instance: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
});

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 响应拦截器
instance.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      // 处理未授权
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default instance;
```

#### 2.2.2 状态管理配置 (store/index.ts)
```typescript
import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
import boardReducer from './slices/boardSlice';
import taskReducer from './slices/taskSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    board: boardReducer,
    task: taskReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
```

#### 2.2.3 路由配置 (config/routes.ts)
```typescript
import { lazy } from 'react';
import { RouteObject } from 'react-router-dom';

const Login = lazy(() => import('@/pages/Login'));
const Board = lazy(() => import('@/pages/Board'));
const Backlog = lazy(() => import('@/pages/Backlog'));

export const routes: RouteObject[] = [
  {
    path: '/login',
    element: <Login />,
  },
  {
    path: '/',
    element: <Layout />,
    children: [
      {
        path: 'board',
        element: <Board />,
      },
      {
        path: 'backlog',
        element: <Backlog />,
      },
    ],
  },
];
```

### 2.3 组件开发规范

#### 2.3.1 组件文件结构
```typescript
// 组件目录结构
ComponentName/
├── index.tsx        # 组件主文件
├── style.module.less # 组件样式
└── types.ts         # 组件类型定义

// 组件代码模板
import React from 'react';
import styles from './style.module.less';
import type { ComponentProps } from './types';

export const ComponentName: React.FC<ComponentProps> = (props) => {
  return (
    <div className={styles.container}>
      {/* 组件内容 */}
    </div>
  );
};
```

#### 2.3.2 Hooks规范
```typescript
// 自定义Hook命名以use开头
export const useCustomHook = () => {
  // Hook逻辑
};

// 异步Hook处理
export const useAsyncHook = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);
  const [data, setData] = useState<Data | null>(null);

  const execute = async () => {
    try {
      setLoading(true);
      // 异步操作
    } catch (err) {
      setError(err as Error);
    } finally {
      setLoading(false);
    }
  };

  return { loading, error, data, execute };
};
```

### 2.4 权限管理实现

#### 2.4.1 角色定义 (types/auth.ts)
```typescript
export enum UserRole {
  ADMIN = 'ADMIN',
  DEVELOPER = 'DEVELOPER'
}

export interface User {
  id: string;
  username: string;
  role: UserRole;
}
```

#### 2.4.2 权限Hook (hooks/usePermission.ts)
```typescript
import { useSelector } from 'react-redux';
import { UserRole } from '@/types/auth';
import type { RootState } from '@/store';

export const usePermission = () => {
  const user = useSelector((state: RootState) => state.auth.user);

  const isAdmin = user?.role === UserRole.ADMIN;
  const isDeveloper = user?.role === UserRole.DEVELOPER;

  const canManageBoard = isAdmin;
  const canConfigureWIP = isAdmin;
  const canManageSprint = isAdmin;
  const canViewAllTasks = isAdmin;
  const canUpdateTask = (taskUserId: string) => isAdmin || user?.id === taskUserId;

  return {
    isAdmin,
    isDeveloper,
    canManageBoard,
    canConfigureWIP,
    canManageSprint,
    canViewAllTasks,
    canUpdateTask,
  };
};
```

#### 2.4.3 权限组件 (components/PermissionGuard/index.tsx)
```typescript
import React from 'react';
import { usePermission } from '@/hooks/usePermission';

interface PermissionGuardProps {
  children: React.ReactNode;
  permission: (perms: ReturnType<typeof usePermission>) => boolean;
}

export const PermissionGuard: React.FC<PermissionGuardProps> = ({
  children,
  permission,
}) => {
  const permissions = usePermission();
  return permission(permissions) ? <>{children}</> : null;
};
```

#### 2.4.4 使用示例
```typescript
// 看板配置组件
const BoardConfig: React.FC = () => {
  const { canManageBoard } = usePermission();

  if (!canManageBoard) {
    return null;
  }

  return (
    <div>
      {/* 看板配置内容 */}
    </div>
  );
};

// 任务列表组件
const TaskList: React.FC = () => {
  const { canViewAllTasks } = usePermission();
  const user = useSelector((state: RootState) => state.auth.user);

  const tasks = useSelector((state: RootState) => state.task.tasks);
  const filteredTasks = canViewAllTasks 
    ? tasks 
    : tasks.filter(task => task.userId === user?.id);

  return (
    <div>
      {filteredTasks.map(task => (
        <TaskCard key={task.id} task={task} />
      ))}
    </div>
  );
};
```

### 2.5 权限控制最佳实践

1. 路由级别权限
   - 使用路由守卫控制页面访问权限
   - 根据用户角色动态生成路由菜单

2. 组件级别权限
   - 使用PermissionGuard组件包装需要权限控制的内容
   - 在组件内部使用usePermission Hook判断权限

3. 数据级别权限
   - 在API请求时携带用户信息
   - 在数据过滤时考虑用户权限
   - 使用Redux中间件处理权限相关的状态更新

4. 错误处理
   - 统一处理401未授权错误
   - 显示友好的权限不足提示
   - 记录权限相关的错误日志

## 3. 功能模块开发指南

### 3.1 认证模块

#### 3.1.1 登录页面实现
```typescript
// pages/Login/index.tsx
import { Form, Input, Button, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';

const Login: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuth();

  const onFinish = async (values: LoginForm) => {
    try {
      await login(values);
      navigate('/board');
    } catch (error) {
      message.error('登录失败');
    }
  };

  return (
    <Form onFinish={onFinish}>
      <Form.Item name="username" rules={[{ required: true }]}>
        <Input placeholder="用户名" />
      </Form.Item>
      <Form.Item name="password" rules={[{ required: true }]}>
        <Input.Password placeholder="密码" />
      </Form.Item>
      <Form.Item>
        <Button type="primary" htmlType="submit">
          登录
        </Button>
      </Form.Item>
    </Form>
  );
};
```

### 3.2 看板模块

#### 3.2.1 看板列表实现
```typescript
// components/Board/BoardList/index.tsx
import { DragDropContext, Droppable } from 'react-beautiful-dnd';
import { useBoard } from '@/hooks/useBoard';

const BoardList: React.FC = () => {
  const { columns, tasks, moveTask } = useBoard();

  const onDragEnd = (result: DropResult) => {
    if (!result.destination) return;
    
    moveTask({
      taskId: result.draggableId,
      sourceColumnId: result.source.droppableId,
      destinationColumnId: result.destination.droppableId,
      destinationIndex: result.destination.index,
    });
  };

  return (
    <DragDropContext onDragEnd={onDragEnd}>
      <div className={styles.boardList}>
        {columns.map(column => (
          <BoardColumn
            key={column.id}
            column={column}
            tasks={tasks.filter(task => task.columnId === column.id)}
          />
        ))}
      </div>
    </DragDropContext>
  );
};
```

### 3.3 任务模块

#### 3.3.1 任务卡片实现
```typescript
// components/Task/TaskCard/index.tsx
import { Card, Tag, Avatar } from 'antd';
import { TaskPriority } from '@/types/models';

const TaskCard: React.FC<TaskCardProps> = ({ task }) => {
  const priorityColors = {
    [TaskPriority.HIGH]: '#f5222d',
    [TaskPriority.MEDIUM]: '#faad14',
    [TaskPriority.LOW]: '#52c41a',
  };

  return (
    <Card className={styles.taskCard}>
      <Tag color={priorityColors[task.priority]}>{task.priority}</Tag>
      <div className={styles.title}>{task.title}</div>
      <div className={styles.description}>{task.description}</div>
      <div className={styles.footer}>
        <Avatar src={task.assignee?.avatar} />
        <span className={styles.dueDate}>{task.dueDate}</span>
      </div>
    </Card>
  );
};
```

## 4. 开发注意事项

### 4.1 代码规范
1. 使用ESLint和Prettier保持代码风格一致
2. 组件使用函数式组件和Hooks
3. 使用TypeScript强类型约束
4. 遵循React最佳实践

### 4.2 性能优化
1. 使用React.memo优化组件重渲染
2. 使用useMemo和useCallback缓存值和函数
3. 实现虚拟列表优化长列表性能
4. 按需加载减小打包体积

### 4.3 测试要求
1. 编写单元测试覆盖核心功能
2. 进行组件集成测试
3. 进行E2E测试验证关键流程

### 4.4 部署相关
1. 配置环境变量区分开发和生产环境
2. 实现CI/CD自动化部署
3. 优化构建配置提升打包效率

## 5. 开发流程

1. 功能开发
   - 创建功能分支
   - 编写代码和测试
   - 提交代码审查
   - 合并到主分支

2. 代码提交
   - 遵循约定式提交规范
   - 提交前进行代码格式化
   - 确保测试通过

3. 发布流程
   - 更新版本号
   - 生成更新日志
   - 构建生产版本
   - 部署到目标环境 