# GIRA前端详细设计文档

## 1. 前端技术架构

### 1.1 技术栈选型
- 核心框架：React 18
- UI组件库：Ant Design 5.x
- 状态管理：Redux Toolkit
- 路由管理：React Router 6
- 类型系统：TypeScript 5.x
- HTTP客户端：Axios
- 样式解决方案：Less + CSS Modules
- 构建工具：Vite
- 代码规范：ESLint + Prettier
- 测试框架：Jest + React Testing Library

### 1.2 项目结构
```
src/
├── assets/          # 静态资源
├── components/      # 通用组件
├── config/          # 配置文件
├── hooks/           # 自定义Hooks
├── layouts/         # 布局组件
├── pages/           # 页面组件
├── services/        # API服务
├── store/           # 状态管理
├── types/           # 类型定义
└── utils/           # 工具函数
```

## 2. 功能模块设计

### 2.1 用户角色定义

系统包含两种用户角色：
1. 管理员（ADMIN）
   - 系统预置角色
   - 完整的系统管理权限
   - 可以创建和管理用户账户
   - 可以管理看板和Sprint
   - 可以进行所有任务操作

2. 开发者（DEVELOPER）
   - 由管理员创建账户
   - 可以在Sprint中创建和更新任务
   - 可以修改个人信息
   - 只能查看看板，无法修改看板结构

### 2.2 登录模块

#### 2.2.1 页面布局
```
+------------------------+
|        GIRA Logo       |
|   +----------------+   |
|   | Username       |   |
|   +----------------+   |
|   | Password       |   |
|   +----------------+   |
|   [    Login     ]     |
+------------------------+
```

#### 2.2.2 功能需求
1. 登录表单
   - 用户名/密码输入
   - 表单验证
   - 错误提示
   - 登录按钮状态管理

2. 状态管理
   - 登录状态维护
   - Token存储
   - 会话超时处理

3. 路由控制
   - 登录拦截
   - 权限控制
   - 登录成功默认跳转到看板页面（/board）
   - 未登录访问任何页面重定向到登录页面

### 2.3 看板模块

#### 2.3.1 看板布局
```
+--------------------------------+
| Board Title    Filter  Search  |
+--------------------------------+
| ToDo | In Progress | Done      |
|------+-------------+-----------|
| Task | Task        | Task      |
| Task | Task        | Task      |
| Task |             |           |
|      |             |           |
+--------------------------------+
```

#### 2.3.2 功能需求
1. 看板配置（管理员或经理）
   - 创建/编辑看板列
   - 设置WIP限制
   - 看板归档
   - 看板状态管理（ACTIVE/ARCHIVED）

2. 任务管理
   - 拖拽排序
   - 状态变更（TODO/IN_PROGRESS/DONE）
   - 任务优先级（LOW/MEDIUM/HIGH）
   - 任务分配

3. 筛选和搜索
   - 按状态筛选
   - 按经办人筛选
   - 按优先级筛选
   - 按关键字搜索

#### 2.3.3 接口调用
1. 看板数据
   - 获取看板列表：GET /api/boards
   - 获取看板详情：GET /api/boards/{id}
   - 获取看板任务：GET /api/boards/{id}/tasks

2. 看板管理（仅管理员）
   - 创建看板：POST /api/boards
   - 更新看板：PUT /api/boards/{id}
   - 更新列配置：PUT /api/boards/{id}/columns
   - 归档看板：PUT /api/boards/{id}/archive

3. 任务操作
   - 创建任务：POST /api/tasks
   - 更新任务：PUT /api/tasks/{id}
   - 更新状态：PUT /api/tasks/{id}/status
   - 删除任务：DELETE /api/tasks/{id}（仅管理员）

### 2.4 Sprint模块

#### 2.4.1 页面布局
```
+--------------------------------+
| Backlog    Create Sprint       |
+--------------------------------+
| Current Sprint                 |
| +--------------------------+   |
| | Task                     |   |
| | Task                     |   |
| +--------------------------+   |
|                                |
| Backlog                       |
| +--------------------------+   |
| | Task                     |   |
| | Task                     |   |
| +--------------------------+   |
+--------------------------------+
```

#### 2.4.2 功能需求
1. Sprint管理（仅管理员）
   - 创建Sprint
   - 开始Sprint
   - 完成Sprint
   - 取消Sprint
   - Sprint状态管理（PLANNING/ACTIVE/COMPLETED）
   - 自动完成过期Sprint（系统自动执行）

2. 任务池
   - 未规划任务列表
   - 拖拽分配任务到Sprint
   - 任务优先级和状态筛选
   - 任务进度跟踪

3. 进度跟踪
   - Sprint状态显示
   - 任务完成进度
   - 剩余工作量
   - 任务统计（总数/已完成）

#### 2.4.3 接口调用
1. Sprint管理
   - 创建Sprint：POST /api/sprints
   - 更新Sprint：PUT /api/sprints/{id}
   - 开始Sprint：PUT /api/sprints/{id}/start
   - 完成Sprint：PUT /api/sprints/{id}/complete
   - 取消Sprint：PUT /api/sprints/{id}/cancel
   - 获取Sprint列表：GET /api/sprints
   - 获取Sprint详情：GET /api/sprints/{id}
   - 获取Sprint任务：GET /api/sprints/{id}/tasks
   - 获取Sprint任务（带筛选）：GET /api/sprints/{id}/tasks?status={status}&priority={priority}

2. 任务管理
   - 获取待办任务：GET /api/tasks/backlog
   - 移动任务到Sprint：PUT /api/tasks/{id}/sprint
   - 获取经办人任务：GET /api/tasks/assignee/{assigneeId}

## 3. UI设计规范

### 3.1 色彩系统
- 主色调：#1890ff
- 辅助色：
  - 成功：#52c41a
  - 警告：#faad14
  - 错误：#f5222d
- 任务优先级：
  - 低：#52c41a
  - 中：#1890ff
  - 高：#faad14
- 中性色：
  - 标题：#262626
  - 正文：#595959
  - 辅助文字：#8c8c8c
  - 边框：#d9d9d9
  - 背景：#f0f2f5

### 3.2 字体系统
- 主要字体：-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto
- 字号：
  - 标题：20px
  - 副标题：16px
  - 正文：14px
  - 辅助文字：12px
- 行高：1.5715

### 3.3 组件设计

#### 3.3.1 任务卡片
```
+------------------------+
| [优先级] 任务标题      |
| 描述文字...            |
|                        |
| [标签] [标签]          |
| 经办人 工时            |
+------------------------+
```

#### 3.3.2 Sprint卡片
```
+------------------------+
| Sprint名称             |
| 目标: xxx              |
| 开始: 2024-01-01      |
| 结束: 2024-01-14      |
| [进度条] 8/10 任务     |
+------------------------+
```

### 3.4 响应式设计
- 断点设置：
  - xs: <576px
  - sm: ≥576px
  - md: ≥768px
  - lg: ≥992px
  - xl: ≥1200px
  - xxl: ≥1600px

- 栅格系统：
  - 容器宽度：1200px
  - 列数：24列
  - 列间距：16px/24px

### 3.5 交互设计

#### 3.5.1 拖拽交互
1. 视觉反馈
   - 拖动时透明度：0.6
   - 可放置区域边框高亮
   - 禁止放置区域显示禁止图标

2. 动画效果
   - 拖动开始：缩放动画
   - 放置结束：平滑过渡
   - 列表重排：高度动画

#### 3.5.2 加载状态
1. 页面加载
   - 使用骨架屏
   - 渐进式加载
   - 预加载关键资源

2. 操作反馈
   - 按钮loading状态
   - 提交等待动画
   - 操作成功/失败提示

#### 3.5.3 错误处理
1. 表单验证
   - 即时验证
   - 聚焦验证
   - 提交验证
   - 自定义验证规则

2. 异常提示
   - 网络错误
   - 权限错误
   - 业务错误
   - 友好的错误提示

## 4. 性能优化

### 4.1 加载优化
1. 代码分割
   - 路由级别分割
   - 组件异步加载
   - 第三方库按需加载

2. 资源优化
   - 图片懒加载
   - 静态资源CDN
   - 资源压缩

### 4.2 运行时优化
1. 渲染优化
   - 虚拟列表
   - 组件缓存
   - 防抖节流

2. 状态管理
   - 精确更新
   - 数据扁平化
   - 缓存策略

## 5. 安全考虑

### 5.1 认证授权
- Token管理
- 权限控制
- 角色管理
- 安全路由

### 5.2 数据安全
- 敏感数据加密
- XSS防护
- CSRF防护
- 输入验证

### 5.3 接口安全
- 请求签名
- 参数校验
- 错误处理
- 超时处理 