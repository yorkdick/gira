# GIRA前端开发详细设计文档

## 1. 通用组件设计

### 1.1 基础布局组件 (BaseLayout)
```typescript
interface BaseLayoutProps {
  children: React.ReactNode;
}

// 布局尺寸规范
const layoutMetrics = {
  header: {
    height: '64px',
    padding: '0 24px'
  },
  sider: {
    width: '200px',
    collapsedWidth: '80px'
  },
  content: {
    padding: '24px',
    maxWidth: '1440px',
    minHeight: 'calc(100vh - 64px)'
  }
}
```

### 1.2 任务卡片组件 (TaskCard)
```typescript
interface TaskCardProps {
  id: string;
  title: string;
  description?: string;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
  status: 'TODO' | 'IN_PROGRESS' | 'DONE';
  assignee?: {
    id: string;
    name: string;
    avatar?: string;
  };
  dueDate?: Date;
  tags?: string[];
  onEdit?: () => void;
  onStatusChange?: (status: string) => void;
  onDelete?: () => void;  // 仅ADMIN
}

// 样式规范
const taskCardStyles = {
  width: '280px',
  padding: '16px',
  margin: '8px 0',
  borderRadius: '4px',
  boxShadow: '0 1px 2px rgba(0, 0, 0, 0.05)',
  transition: 'all 0.3s',
  
  hover: {
    boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)'
  },
  
  priorityIndicator: {
    high: '#f5222d',
    medium: '#faad14',
    low: '#52c41a'
  }
}
```

## 2. 页面详细设计

### 2.1 登录页面 (/login)

#### 布局设计
```typescript
const LoginPageLayout = {
  container: {
    height: '100vh',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    background: '#f0f2f5'
  },
  
  form: {
    width: '400px',
    padding: '32px',
    background: '#fff',
    borderRadius: '4px',
    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)'
  }
}
```

#### UI操作设计
```typescript
interface LoginOperations {
  // 表单提交
  onSubmit: (values: LoginForm) => void;
}

interface LoginForm {
  username: string;
  password: string;
}
```

### 2.2 看板页面 (/board)

#### 布局设计
```typescript
const BoardPageLayout = {
  container: {
    display: 'flex',
    gap: '24px',
    padding: '24px',
    height: 'calc(100vh - 64px)',
    overflow: 'auto'
  },
  
  column: {
    width: '300px',
    minHeight: '600px',
    background: '#f5f5f5',
    borderRadius: '4px',
    padding: '16px'
  }
}
```

#### UI操作设计
```typescript
interface BoardOperations {
  // 拖拽操作
  onDragStart: (taskId: string) => void;
  onDragOver: (e: DragEvent) => void;
  onDrop: (taskId: string, newStatus: string) => void;
  
  // 任务操作
  onTaskClick: (taskId: string) => void;
}
```

### 2.3 Sprint页面 (/sprints)

#### 布局设计
```typescript
const SprintPageLayout = {
  container: {
    padding: '24px'
  },
  
  header: {
    marginBottom: '24px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
  },
  
  sprintCard: {
    marginBottom: '24px',
    background: '#fff',
    borderRadius: '4px',
    padding: '24px'
  },
  
  taskList: {
    display: 'grid',
    gap: '16px',
    gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))'
  }
}
```

#### UI操作设计
```typescript
interface SprintOperations {
  // Sprint操作
  onSearch: (keyword: string) => void;  // 搜索Sprint
  onCreateSprint: () => void;           // 仅ADMIN
  onStartSprint: (sprintId: string) => void;     // 仅ADMIN
  onCompleteSprint: (sprintId: string) => void;  // 仅ADMIN
  onEditSprint: (sprintId: string) => void;      // 仅ADMIN
  
  // 任务操作
  onCreateTask: () => void;
  onEditTask: (taskId: string) => void;
  onUpdateTaskStatus: (taskId: string, status: string) => void;
  onDeleteTask: (taskId: string) => void;  // 仅ADMIN
}
```

### 2.4 用户管理页面 (/users)

#### 布局设计
```typescript
const UserManagementLayout = {
  container: {
    padding: '24px'
  },
  
  toolbar: {
    marginBottom: '24px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
  },
  
  table: {
    background: '#fff',
    borderRadius: '4px'
  }
}
```

#### UI操作设计
```typescript
interface UserManagementOperations {
  // 用户操作
  onCreateUser: () => void;           // 仅ADMIN
  onEditUser: (userId: string) => void;
  onDeleteUser: (userId: string) => void;  // 仅ADMIN
}
```

### 2.5 看板管理页面 (/boards)

#### 布局设计
```typescript
const BoardManagementLayout = {
  container: {
    padding: '24px'
  },
  
  toolbar: {
    marginBottom: '24px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
  },
  
  table: {
    background: '#fff',
    borderRadius: '4px'
  }
}
```

#### UI操作设计
```typescript
interface BoardManagementOperations {
  // 看板操作
  onEditBoard: (boardId: string) => void;  // 仅ADMIN
  onUpdateBoard: (boardId: string, data: UpdateBoardRequest) => void;  // 仅ADMIN
}
```

### 2.6 个人设置页面 (/settings)

#### 布局设计
```typescript
const UserSettingsLayout = {
  container: {
    width: '400px',
    padding: '24px',
    background: '#fff',
    borderRadius: '4px',
    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)'
  },
  
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '16px'
  }
}
```

#### UI操作设计
```typescript
interface UserSettingsOperations {
  // 个人信息操作
  onUpdateProfile: (data: UpdateUserRequest) => void;
  onUpdatePassword: (data: UpdatePasswordRequest) => void;
}
```

## 3. 响应式设计

### 3.1 断点设计
```typescript
const breakpoints = {
  xs: '480px',
  sm: '576px',
  md: '768px',
  lg: '992px',
  xl: '1200px',
  xxl: '1600px'
}
```

### 3.2 响应式布局规则
```typescript
const responsiveRules = {
  // 看板页面
  board: {
    xs: 'stack', // 垂直堆叠列
    sm: 'stack',
    md: 'scroll', // 水平滚动
    lg: 'flex', // 并排显示
    xl: 'flex'
  },
  
  // Sprint页面
  sprint: {
    taskList: {
      xs: '1列',
      sm: '2列',
      md: '3列',
      lg: '4列',
      xl: '5列'
    }
  }
}
```

## 4. 交互动画设计

### 4.1 过渡动画
```typescript
const transitions = {
  // 页面切换
  page: {
    duration: '300ms',
    timing: 'ease-in-out',
    properties: ['opacity', 'transform']
  },
  
  // 任务卡片
  taskCard: {
    duration: '200ms',
    timing: 'ease-out',
    properties: ['box-shadow', 'transform']
  },
  
  // 拖拽
  drag: {
    duration: '150ms',
    timing: 'ease',
    properties: ['opacity', 'transform']
  }
}
```

### 4.2 交互反馈
```typescript
const feedback = {
  // 点击反馈
  click: {
    ripple: {
      color: 'rgba(0, 0, 0, 0.15)',
      duration: '350ms'
    }
  },
  
  // 拖拽反馈
  drag: {
    opacity: 0.6,
    scale: 1.05,
    shadow: '0 8px 16px rgba(0, 0, 0, 0.1)'
  }
} 