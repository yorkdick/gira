# GIRA项目前端设计文档

## 1. 技术栈选型

### 1.1 核心框架和工具
- React: 18.2.0
  - 使用函数组件和Hooks
  - 严格模式(Strict Mode)开启
  - 支持Suspense和并发模式
- TypeScript: 5.3.0
  - 严格模式配置(strict: true)
  - 强制类型检查
  - 完整的类型定义

### 1.2 状态管理
- Redux Toolkit: 2.0.0
  - 使用createSlice管理状态
  - 使用RTK Query处理API请求
  - 配合Redux Persist实现状态持久化
  - 支持TypeScript类型推导

### 1.3 路由管理
- React Router: 6.21.0
  - 使用新的数据路由API
  - 实现路由级别的代码分割
  - 支持路由鉴权

### 1.4 UI组件库
- Ant Design: 5.12.0
  - 使用Less自定义主题
  - 按需加载组件
  - 配置组件全局化配置（ConfigProvider）
  - 支持暗黑模式

### 1.5 开发工具链
- Vite: 5.0.0
  - 使用ESBuild进行构建
  - 配置模块热替换(HMR)
  - 优化构建性能
- ESLint: 8.55.0
  - 使用airbnb规范
  - TypeScript支持
  - 自动修复能力
- Prettier: 3.1.0
  - 统一代码风格
  - 集成编辑器插件
- Husky: 8.0.3
  - Git提交前代码检查
  - 自动化代码规范
- Commitlint: 18.4.0
  - Git提交信息规范
  - 自定义提交规则

## 2. 项目结构

```typescript
src/
├── assets/                 # 静态资源
│   ├── images/            # 图片资源
│   └── styles/            # 全局样式
├── components/            # 通用组件
│   ├── common/           # 基础组件
│   │   ├── Button/      # 按钮组件
│   │   ├── Input/       # 输入框组件
│   │   └── Modal/       # 弹窗组件
│   └── business/         # 业务组件
│       ├── TaskCard/    # 任务卡片
│       ├── BoardColumn/ # 看板列
│       └── SprintCard/  # Sprint卡片
├── config/               # 配置文件
│   ├── routes.ts        # 路由配置
│   └── constants.ts     # 常量配置
├── hooks/               # 自定义Hooks
│   ├── useAuth.ts      # 认证相关
│   ├── useBoard.ts     # 看板相关
│   └── useTask.ts      # 任务相关
├── layouts/             # 布局组件
│   ├── MainLayout/     # 主布局
│   └── AuthLayout/     # 认证布局
├── pages/              # 页面组件
│   ├── auth/           # 认证相关
│   ├── board/          # 看板相关
│   ├── sprint/         # Sprint相关
│   └── task/           # 任务相关
├── services/           # API服务
│   ├── auth.ts        # 认证API
│   ├── board.ts       # 看板API
│   └── task.ts        # 任务API
├── store/             # Redux状态管理
│   ├── slices/        # Redux切片
│   └── hooks.ts       # Redux Hooks
├── types/             # TypeScript类型定义
├── utils/             # 工具函数
└── App.tsx           # 应用入口
```

## 3. 开发规范

### 3.1 命名规范

#### 文件命名
- 组件文件：使用PascalCase
- 工具文件：使用camelCase
- 样式文件：使用kebab-case
```typescript
// 组件文件
components/Button/index.tsx
components/Button/style.module.less

// 工具文件
utils/formatDate.ts
utils/localStorage.ts

// 样式文件
styles/global-variables.less
styles/reset.less
```

#### 组件命名
```typescript
// 函数组件
const UserProfile: React.FC = () => { ... }

// Hook命名
const useTaskStatus = () => { ... }

// 类型命名
interface UserProps { ... }
type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE'
```

### 3.2 样式规范

#### CSS Modules
```less
// 使用CSS Modules
.container {
  &-header {
    display: flex;
    align-items: center;
  }
  
  &-content {
    padding: 16px;
  }
}
```

#### 主题变量
```less
// 主题变量定义
@primary-color: #1890ff;
@success-color: #52c41a;
@warning-color: #faad14;
@error-color: #f5222d;

// 任务优先级颜色
@priority-high: @error-color;
@priority-medium: @warning-color;
@priority-low: @success-color;

// 字体定义
@font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto;
@font-size-base: 14px;
@font-size-lg: 16px;
@font-size-sm: 12px;
```

### 3.3 组件开发规范

#### 函数组件模板
```typescript
import React from 'react';
import styles from './style.module.less';

interface Props {
  title: string;
  onAction?: () => void;
}

export const Component: React.FC<Props> = ({ 
  title,
  onAction 
}) => {
  // hooks声明
  const [state, setState] = useState<string>('');
  
  // 业务逻辑
  const handleClick = () => {
    setState('new value');
    onAction?.();
  };
  
  return (
    <div className={styles.container}>
      <h1>{title}</h1>
      <button onClick={handleClick}>
        Click me
      </button>
    </div>
  );
};
```

## 4. 状态管理规范

### 4.1 Redux Slice示例
```typescript
import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface TaskState {
  tasks: Task[];
  loading: boolean;
  error: string | null;
}

const initialState: TaskState = {
  tasks: [],
  loading: false,
  error: null,
};

const taskSlice = createSlice({
  name: 'tasks',
  initialState,
  reducers: {
    setTasks: (state, action: PayloadAction<Task[]>) => {
      state.tasks = action.payload;
    },
    addTask: (state, action: PayloadAction<Task>) => {
      state.tasks.push(action.payload);
    },
    updateTask: (state, action: PayloadAction<Task>) => {
      const index = state.tasks.findIndex(task => task.id === action.payload.id);
      if (index !== -1) {
        state.tasks[index] = action.payload;
      }
    },
  },
});
```

### 4.2 API请求规范
```typescript
// RTK Query API定义
import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

export const api = createApi({
  baseQuery: fetchBaseQuery({ 
    baseUrl: '/api',
    prepareHeaders: (headers) => {
      const token = localStorage.getItem('token');
      if (token) {
        headers.set('authorization', `Bearer ${token}`);
      }
      return headers;
    },
  }),
  endpoints: (builder) => ({
    getTasks: builder.query<Task[], void>({
      query: () => 'tasks',
    }),
    getTaskById: builder.query<Task, string>({
      query: (id) => `tasks/${id}`,
    }),
    createTask: builder.mutation<Task, Partial<Task>>({
      query: (task) => ({
        url: 'tasks',
        method: 'POST',
        body: task,
      }),
    }),
  }),
});
```

## 5. 性能优化策略

### 5.1 代码分割
```typescript
// 路由级别代码分割
const Board = React.lazy(() => import('./pages/Board'));
const Sprint = React.lazy(() => import('./pages/Sprint'));
const Task = React.lazy(() => import('./pages/Task'));

// 路由配置
const routes = [
  {
    path: '/board',
    element: (
      <Suspense fallback={<Loading />}>
        <Board />
      </Suspense>
    ),
  },
];
```

### 5.2 组件优化
```typescript
// 使用React.memo避免不必要的重渲染
const TaskCard = React.memo<TaskProps>(({ task }) => {
  return (/* ... */);
});

// 使用useMemo和useCallback缓存值和函数
const memoizedValue = useMemo(
  () => computeExpensiveValue(a, b), 
  [a, b]
);

const memoizedCallback = useCallback(
  () => { doSomething(a, b); }, 
  [a, b]
);
```

### 5.3 图片优化
```typescript
// 使用现代图片格式
<img 
  src="image.webp"
  srcSet="image-1x.webp 1x, image-2x.webp 2x"
  loading="lazy"
  alt="description"
/>
```

## 6. 测试规范

### 6.1 单元测试
```typescript
// Jest + React Testing Library
import { render, screen, fireEvent } from '@testing-library/react';

describe('TaskCard', () => {
  it('should render task title', () => {
    render(<TaskCard task={mockTask} />);
    expect(screen.getByText(mockTask.title)).toBeInTheDocument();
  });
  
  it('should handle status change', () => {
    const onStatusChange = jest.fn();
    render(
      <TaskCard 
        task={mockTask} 
        onStatusChange={onStatusChange} 
      />
    );
    fireEvent.click(screen.getByRole('button'));
    expect(onStatusChange).toHaveBeenCalled();
  });
});
```

### 6.2 集成测试
```typescript
// 测试Redux集成
import { renderWithProviders } from '../test-utils';

test('shows task list', async () => {
  renderWithProviders(<TaskList />);
  
  expect(await screen.findByRole('list')).toBeInTheDocument();
  expect(screen.getAllByRole('listitem')).toHaveLength(3);
});
```

## 7. 构建和部署

### 7.1 构建配置
```typescript
// vite.config.ts
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  build: {
    target: 'es2015',
    minify: 'terser',
    sourcemap: true,
    chunkSizeWarningLimit: 2000,
  },
  css: {
    modules: {
      localsConvention: 'camelCase',
    },
    preprocessorOptions: {
      less: {
        javascriptEnabled: true,
        modifyVars: {
          '@primary-color': '#1890ff',
        },
      },
    },
  },
});
```

### 7.2 环境配置
```typescript
// .env.development
VITE_API_URL=http://localhost:8080/api
VITE_ENV=development

// .env.production
VITE_API_URL=/api
VITE_ENV=production
```

## 8. 代码提交规范

### 8.1 Commitlint配置
```javascript
// commitlint.config.js
module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'type-enum': [
      2,
      'always',
      [
        'feat',     // 新功能
        'fix',      // 修复
        'docs',     // 文档
        'style',    // 样式
        'refactor', // 重构
        'test',     // 测试
        'chore',    // 构建过程或辅助工具的变动
      ],
    ],
  },
};
```

### 8.2 ESLint配置
```javascript
// .eslintrc.js
module.exports = {
  extends: [
    'airbnb',
    'airbnb-typescript',
    'plugin:@typescript-eslint/recommended',
    'plugin:prettier/recommended',
  ],
  rules: {
    'react/react-in-jsx-scope': 'off',
    'react/require-default-props': 'off',
    '@typescript-eslint/no-unused-vars': 'error',
  },
};
```

## 9. 安全规范

### 9.1 认证和授权
- 使用JWT进行身份验证
- 实现路由级别的权限控制
- 敏感信息不存储在localStorage

### 9.2 数据传输
- 使用HTTPS进行数据传输
- 实现CSRF防护
- API请求添加超时处理

### 9.3 输入验证
- 实现前端数据验证
- 防止XSS攻击
- 实现Rate Limiting