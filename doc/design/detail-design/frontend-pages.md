# GIRA项目前端页面设计文档

## 1. 页面布局

### 1.1 基础布局
```typescript
Layout
├── Header                // 顶部导航栏
│   ├── Logo             // 项目Logo
│   ├── Navigation       // 主导航菜单
│   └── UserMenu        // 用户菜单（包含个人信息、登出功能）
├── Sider                // 左侧边栏
│   └── SubNavigation   // 子导航菜单
└── Content              // 主内容区域
    └── PageContainer   // 页面容器
```

### 1.2 响应式设计
- 桌面端（≥1200px）：三栏布局

## 2. 页面分组

### 2.1 认证页面组
#### 登录页面（/login）
- 布局：居中单列布局
- 功能：
  - 用户名密码登录
  - 登出（在顶部导航栏的用户菜单中）
- 权限：所有用户
- 参考页面：[登录页面设计图](../../../doc/assets/image/login.png)

### 2.2 看板页面
#### 当前看板页（/activeBoard）
- 布局：三列布局(代办、进行中、完成)
- 功能：
  - 拖动任务卡片更新任务状态
  - 点击任务卡片查看任务详情
  - 编辑任务信息（标题、描述、经办人、优先级）
- 权限：
  - ADMIN：全部功能
  - DEVELOPER：查看和编辑功能
- 参考页面：[看板页面设计图](../../../doc/assets/image/JIRA-Kanban.png)

### 2.3 Sprint页面
#### Sprint页面（/sprints）
- 布局：垂直列表布局
  - 顶部操作栏
    - 创建Sprint按钮（ADMIN）
    - 搜索框（按Sprint名称搜索）
  - Sprint卡片列表
    - Sprint信息区
      - Sprint名称和日期
      - Sprint状态
      - 操作按钮（开始/完成Sprint）
    - 任务列表区
      - 任务卡片（按优先级排序显示）
        - 任务标题
        - 任务状态（TODO/IN_PROGRESS/DONE）
        - 优先级标识
        - 经办人名称缩写
      - 快速创建任务按钮
- 功能：
  - Sprint管理
    - 搜索Sprint（按Sprint名称搜索）
    - 创建新Sprint（ADMIN）
    - 开始Sprint（ADMIN）
    - 完成Sprint（ADMIN）
    - Sprint信息编辑（名称、开始结束日期）（ADMIN）
  - 任务管理
    - 查看Sprint任务列表（按优先级排序）
    - 创建新任务
    - 编辑任务（标题、描述、经办人、优先级）
    - 更新任务状态（TODO/IN_PROGRESS/DONE）
    - 删除任务（ADMIN）
- 权限：
  - ADMIN：全部功能
  - DEVELOPER：查看和任务操作
- 参考页面：[Sprint页面设计图](../../../doc/assets/image/JIRA-Backlog.png)

### 2.4 用户管理页面组  (只有ADMIN可以访问)
#### 用户列表页（/users）
- 布局：表格布局
- 功能：
  - 查看所有用户
  - 创建新用户（ADMIN）
  - 编辑用户信息（邮箱、全名、状态）
  - 删除用户（ADMIN）
- 权限：仅ADMIN

#### 个人设置页（/settings）
- 布局：点击个人头像，表单弹出框
- 功能：
  - 查看个人信息
  - 修改个人信息（邮箱、全名）
  - 修改密码
  - 获取当前用户信息
- 权限：所有用户

## 2.5 看板管理页面组  (只有ADMIN可以访问)
#### 看板列表页（/boards）
- 布局：表格布局
- 功能：
  - 查看所有看板
  - 编辑看板信息（名称、描述）
- 权限：仅ADMIN

## 3. UI设计规范

### 3.1 颜色系统
```less
// 主题色
@primary-color: #1890ff;        // 主色
@success-color: #52c41a;        // 成功色
@warning-color: #faad14;        // 警告色
@error-color: #f5222d;          // 错误色

// 中性色
@heading-color: #262626;        // 标题色
@text-color: #595959;           // 正文色
@text-color-secondary: #8c8c8c; // 次要文字
@disabled-color: #bfbfbf;       // 禁用色
@border-color: #d9d9d9;         // 边框色
@background-color: #f0f2f5;     // 背景色

// 任务状态色
@task-todo: #bfbfbf;           // 待办
@task-in-progress: #1890ff;    // 进行中
@task-done: #52c41a;           // 已完成

// 任务优先级色
@priority-high: @error-color;
@priority-medium: @warning-color;
@priority-low: @success-color;
```

### 3.2 字体系统
```less
// 字体家族
@font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto;

// 字号
@font-size-base: 14px;         // 基础字号
@font-size-lg: 16px;           // 大号
@font-size-sm: 12px;           // 小号
@heading-1-size: 38px;         // 一级标题
@heading-2-size: 30px;         // 二级标题
@heading-3-size: 24px;         // 三级标题
@heading-4-size: 20px;         // 四级标题

// 行高
@line-height-base: 1.5715;
```

### 3.3 间距系统
```less
// 基础间距
@spacing-unit: 4px;            // 基础单位
@spacing-xs: @spacing-unit;     // 4px
@spacing-sm: @spacing-unit * 2; // 8px
@spacing-md: @spacing-unit * 4; // 16px
@spacing-lg: @spacing-unit * 6; // 24px
@spacing-xl: @spacing-unit * 8; // 32px

// 内容区域
@content-padding: @spacing-lg;  // 24px
@card-padding: @spacing-md;     // 16px
```

### 3.4 组件样式

#### 卡片样式
```less
.card {
  background: #fff;
  border-radius: 2px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  padding: @card-padding;
}
```

#### 任务卡片
```less
.task-card {
  .card();
  margin-bottom: @spacing-sm;
  
  &-title {
    font-size: @font-size-base;
    font-weight: 500;
    margin-bottom: @spacing-sm;
  }
  
  &-meta {
    color: @text-color-secondary;
    font-size: @font-size-sm;
  }
  
  &-priority {
    &-high { color: @priority-high; }
    &-medium { color: @priority-medium; }
    &-low { color: @priority-low; }
  }
}
```

#### 看板列
```less
.board-column {
  background: #f5f5f5;
  border-radius: 2px;
  padding: @spacing-md;
  width: 280px;
  
  &-header {
    margin-bottom: @spacing-md;
    padding: @spacing-sm;
    
    h3 {
      font-size: @font-size-lg;
      margin: 0;
    }
  }
  
  &-content {
    min-height: 200px;
  }
}
```

### 3.5 响应式断点
```less
@screen-xs: 480px;
@screen-sm: 576px;
@screen-md: 768px;
@screen-lg: 992px;
@screen-xl: 1200px;
@screen-xxl: 1600px;
```

## 4. 交互设计

### 4.1 拖拽交互
- 任务卡片支持拖拽
- 看板列支持拖拽排序
- 拖拽时显示半透明效果
- 拖拽目标区域高亮显示

### 4.2 加载状态
- 页面切换时显示顶部进度条
- 数据加载时显示骨架屏
- 按钮操作时显示加载图标
- 表单提交时禁用提交按钮

### 4.3 反馈机制
- 操作成功显示成功提示
- 操作失败显示错误信息
- 重要操作需要二次确认
- 表单验证即时反馈

### 4.4 动画效果
- 页面切换淡入淡出
- 列表项增删渐变效果
- 卡片展开收起动画
- 悬浮效果反馈 