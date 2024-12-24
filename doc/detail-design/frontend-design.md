# GIRA 前端详细设计文档

## 1. 项目结构

### 1.1 目录结构
```
gira-frontend/
├── src/
│   ├── assets/           # 静态资源
│   ├── components/       # 通用组件
│   ├── features/         # 功能模块
│   ├── hooks/            # 自定义Hook
│   ├── layouts/          # 布局组件
│   ├── pages/            # 页面组件
│   ├── services/         # API服务
│   ├── store/            # 状态管理
│   ├── styles/           # 样式文件
│   ├── types/            # TypeScript类型
│   └── utils/            # 工具函数
├── public/               # 公共资源
└── tests/                # 测试文件
```

### 1.2 功能模块划分
```
features/
├── auth/                # 认证模块
├── backlog/             # 需求管理
├── board/               # 看板管理
├── project/             # 项目管理
├── user/                # 用户管理
└── settings/            # 系统设置
```

## 2. 组件设计

### 2.1 布局组件
```tsx
// layouts/MainLayout.tsx
import React from 'react';
import { Layout } from 'antd';
import { Sidebar, Header, Footer } from '@/components';

const MainLayout: React.FC = ({ children }) => {
  return (
    <Layout>
      <Layout.Sider>
        <Sidebar />
      </Layout.Sider>
      <Layout>
        <Layout.Header>
          <Header />
        </Layout.Header>
        <Layout.Content>
          {children}
        </Layout.Content>
        <Layout.Footer>
          <Footer />
        </Layout.Footer>
      </Layout>
    </Layout>
  );
};
```

### 2.2 通用组件

#### 2.2.1 表单组件
```tsx
// components/form/IssueForm.tsx
import React from 'react';
import { Form, Input, Select, DatePicker } from 'antd';
import type { Issue } from '@/types';

interface IssueFormProps {
  initialValues?: Partial<Issue>;
  onSubmit: (values: Issue) => void;
}

const IssueForm: React.FC<IssueFormProps> = ({ initialValues, onSubmit }) => {
  return (
    <Form
      layout="vertical"
      initialValues={initialValues}
      onFinish={onSubmit}
    >
      <Form.Item
        name="title"
        label="标题"
        rules={[{ required: true }]}
      >
        <Input />
      </Form.Item>
      <Form.Item
        name="description"
        label="描述"
      >
        <Input.TextArea rows={4} />
      </Form.Item>
      {/* 其他表单项 */}
    </Form>
  );
};
```

#### 2.2.2 列表组件
```tsx
// components/list/IssueList.tsx
import React from 'react';
import { List, Card } from 'antd';
import type { Issue } from '@/types';

interface IssueListProps {
  issues: Issue[];
  onIssueClick: (issue: Issue) => void;
}

const IssueList: React.FC<IssueListProps> = ({ issues, onIssueClick }) => {
  return (
    <List
      dataSource={issues}
      renderItem={(issue) => (
        <List.Item onClick={() => onIssueClick(issue)}>
          <Card title={issue.title}>
            <p>{issue.description}</p>
            <div className="issue-meta">
              <span>{issue.status}</span>
              <span>{issue.priority}</span>
            </div>
          </Card>
        </List.Item>
      )}
    />
  );
};
```

### 2.3 业务组件

#### 2.3.1 看板组件
```tsx
// features/board/components/KanbanBoard.tsx
import React from 'react';
import { DragDropContext, Droppable } from 'react-beautiful-dnd';
import { KanbanColumn } from './KanbanColumn';
import type { Issue, Column } from '@/types';

interface KanbanBoardProps {
  columns: Column[];
  issues: Issue[];
  onDragEnd: (result: any) => void;
}

const KanbanBoard: React.FC<KanbanBoardProps> = ({
  columns,
  issues,
  onDragEnd,
}) => {
  return (
    <DragDropContext onDragEnd={onDragEnd}>
      <div className="kanban-board">
        {columns.map((column) => (
          <Droppable key={column.id} droppableId={column.id}>
            {(provided) => (
              <KanbanColumn
                column={column}
                issues={issues.filter((i) => i.status === column.id)}
                provided={provided}
              />
            )}
          </Droppable>
        ))}
      </div>
    </DragDropContext>
  );
};
```

## 3. 状态管理

### 3.1 Redux Store配置
```typescript
// store/index.ts
import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
import projectReducer from './slices/projectSlice';
import issueReducer from './slices/issueSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    project: projectReducer,
    issue: issueReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
```

### 3.2 状态切片示例
```typescript
// store/slices/issueSlice.ts
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { issueApi } from '@/services/api';
import type { Issue } from '@/types';

export const fetchIssues = createAsyncThunk(
  'issue/fetchIssues',
  async (projectId: string) => {
    const response = await issueApi.getIssues(projectId);
    return response.data;
  }
);

const issueSlice = createSlice({
  name: 'issue',
  initialState: {
    issues: [] as Issue[],
    loading: false,
    error: null as string | null,
  },
  reducers: {
    // 同步action
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchIssues.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchIssues.fulfilled, (state, action) => {
        state.issues = action.payload;
        state.loading = false;
      })
      .addCase(fetchIssues.rejected, (state, action) => {
        state.error = action.error.message || null;
        state.loading = false;
      });
  },
});
```

## 4. API服务

### 4.1 Axios配置
```typescript
// services/axios.ts
import axios from 'axios';
import { message } from 'antd';

const axiosInstance = axios.create({
  baseURL: process.env.REACT_APP_API_URL,
  timeout: 10000,
});

axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 处理未授权
          break;
        case 403:
          // 处理禁止访问
          break;
        default:
          message.error('请求失败');
      }
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
```

### 4.2 API服务封装
```typescript
// services/api/issue.ts
import axios from '../axios';
import type { Issue, IssueCreateRequest, IssueUpdateRequest } from '@/types';

export const issueApi = {
  getIssues: (projectId: string) =>
    axios.get<Issue[]>(`/api/projects/${projectId}/issues`),
    
  getIssue: (issueId: string) =>
    axios.get<Issue>(`/api/issues/${issueId}`),
    
  createIssue: (data: IssueCreateRequest) =>
    axios.post<Issue>('/api/issues', data),
    
  updateIssue: (issueId: string, data: IssueUpdateRequest) =>
    axios.put<Issue>(`/api/issues/${issueId}`, data),
    
  deleteIssue: (issueId: string) =>
    axios.delete(`/api/issues/${issueId}`),
};
```

## 5. 路由配置

### 5.1 路由定义
```typescript
// routes/index.tsx
import { createBrowserRouter } from 'react-router-dom';
import { MainLayout } from '@/layouts';
import {
  LoginPage,
  DashboardPage,
  ProjectListPage,
  ProjectDetailPage,
  BacklogPage,
  BoardPage,
  SettingsPage,
} from '@/pages';

export const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/',
    element: <MainLayout />,
    children: [
      {
        path: 'dashboard',
        element: <DashboardPage />,
      },
      {
        path: 'projects',
        element: <ProjectListPage />,
      },
      {
        path: 'projects/:projectId',
        element: <ProjectDetailPage />,
      },
      {
        path: 'projects/:projectId/backlog',
        element: <BacklogPage />,
      },
      {
        path: 'projects/:projectId/board',
        element: <BoardPage />,
      },
      {
        path: 'settings',
        element: <SettingsPage />,
      },
    ],
  },
]);
```

## 6. 样式管理

### 6.1 TailwindCSS配置
```javascript
// tailwind.config.js
module.exports = {
  content: ['./src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: '#1890ff',
        success: '#52c41a',
        warning: '#faad14',
        error: '#f5222d',
      },
      spacing: {
        // 自定义间距
      },
    },
  },
  plugins: [],
};
```

### 6.2 全局样式
```scss
// styles/global.scss
@tailwind base;
@tailwind components;
@tailwind utilities;

// 自定义样式
@layer components {
  .btn-primary {
    @apply px-4 py-2 bg-primary text-white rounded-md;
    &:hover {
      @apply bg-primary-dark;
    }
  }
  
  .card {
    @apply p-4 bg-white rounded-lg shadow-md;
  }
}
```

## 7. 测试配置

### 7.1 Jest配置
```javascript
// jest.config.js
module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'jsdom',
  setupFilesAfterEnv: ['<rootDir>/src/setupTests.ts'],
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/$1',
    '\\.(css|less|scss)$': 'identity-obj-proxy',
  },
};
```

### 7.2 测试示例
```typescript
// components/__tests__/IssueForm.test.tsx
import { render, screen, fireEvent } from '@testing-library/react';
import { IssueForm } from '../IssueForm';

describe('IssueForm', () => {
  it('should render form fields correctly', () => {
    render(<IssueForm onSubmit={jest.fn()} />);
    
    expect(screen.getByLabelText('标题')).toBeInTheDocument();
    expect(screen.getByLabelText('描述')).toBeInTheDocument();
  });
  
  it('should call onSubmit with form values', async () => {
    const onSubmit = jest.fn();
    render(<IssueForm onSubmit={onSubmit} />);
    
    fireEvent.change(screen.getByLabelText('标题'), {
      target: { value: 'Test Issue' },
    });
    
    fireEvent.click(screen.getByText('���交'));
    
    expect(onSubmit).toHaveBeenCalledWith(
      expect.objectContaining({
        title: 'Test Issue',
      })
    );
  });
});
```

## 8. 性能优化

### 8.1 代码分割
```typescript
// App.tsx
import { lazy, Suspense } from 'react';
import { Loading } from '@/components';

const Dashboard = lazy(() => import('@/pages/Dashboard'));
const ProjectList = lazy(() => import('@/pages/ProjectList'));
const Board = lazy(() => import('@/pages/Board'));

const App = () => {
  return (
    <Suspense fallback={<Loading />}>
      {/* 路由配置 */}
    </Suspense>
  );
};
```

### 8.2 性能监控
```typescript
// utils/performance.ts
export const measurePerformance = (componentName: string) => {
  const startTime = performance.now();
  
  return () => {
    const endTime = performance.now();
    console.log(`${componentName} render time: ${endTime - startTime}ms`);
  };
};
```

## 9. 国际化

### 9.1 配置
```typescript
// i18n/config.ts
import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

i18n
  .use(initReactI18next)
  .init({
    resources: {
      en: {
        translation: require('./locales/en.json'),
      },
      zh: {
        translation: require('./locales/zh.json'),
      },
    },
    lng: 'zh',
    fallbackLng: 'en',
    interpolation: {
      escapeValue: false,
    },
  });

export default i18n;
```

### 9.2 使用示例
```typescript
// components/Header.tsx
import { useTranslation } from 'react-i18next';

const Header = () => {
  const { t } = useTranslation();
  
  return (
    <header>
      <h1>{t('header.title')}</h1>
      <nav>
        <a href="/dashboard">{t('nav.dashboard')}</a>
        <a href="/projects">{t('nav.projects')}</a>
      </nav>
    </header>
  );
};
``` 