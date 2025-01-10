# GIRA前端开发任务列表

> 状态说明：
> - 🔲 未开始
> - ⏳ 进行中
> - ✅ 已完成

## 1. 环境搭建 [✅]
1. [x] 安装开发环境
   - [x] 安装Node.js v18+
   - [x] 安装pnpm
   - [x] 安装VSCode及插件

2. [x] 创建项目
   - [x] 使用Vite创建项目
   - [x] 配置vite.config.ts
   - [x] 配置tsconfig.json

3. [x] 安装依赖
   - [x] 安装核心依赖
   ```bash
   pnpm add react@18 react-dom@18 @types/react @types/react-dom
   ```
   - [x] 安装UI组件库
   ```bash
   pnpm add antd @ant-design/icons
   ```
   - [x] 安装状态管理
   ```bash
   pnpm add @reduxjs/toolkit react-redux
   ```
   - [x] 安装路由
   ```bash
   pnpm add react-router-dom
   ```
   - [x] 安装HTTP客户端
   ```bash
   pnpm add axios
   ```
   - [x] 安装开发依赖
   ```bash
   pnpm add -D typescript @typescript-eslint/parser @typescript-eslint/eslint-plugin
   pnpm add -D eslint prettier eslint-config-prettier eslint-plugin-prettier
   pnpm add -D less
   ```

## 2. 项目基础架构 [✅]
1. [x] 创建目录结构
   ```
   src/
   ├── assets/       # 静态资源
   ├── components/   # 通用组件
   ├── config/       # 配置文件
   ├── hooks/        # 自定义Hooks
   ├── layouts/      # 布局组件
   ├── pages/        # 页面组件
   ├── services/     # API服务
   ├── store/        # 状态管理
   ├── types/        # 类型定义
   └── utils/        # 工具函数
   ```

2. [x] 配置工具类
   - [x] utils/request.ts (Axios封装)
   - [x] utils/storage.ts (本地存储)
   - [x] utils/auth.ts (认证工具)

3. [x] 配置状态管理
   - [x] store/index.ts (Store配置)
   - [x] store/slices/authSlice.ts (认证状态)
   - [x] store/slices/userSlice.ts (用户状态)

4. [x] 配置路由
   - [x] config/routes.ts (路由配置)
   - [x] components/PrivateRoute.tsx (权限路由)

## 3. 认证模块 [✅]
1. [x] 类型定义
   - [x] types/auth.ts (认证相关类型)
   - [x] types/user.ts (用户相关类型)

2. [x] API服务
   - [x] services/auth.ts (认证API)
   - [x] services/user.ts (用户API)

3. [x] 登录页面
   - [x] pages/Login/index.tsx (登录页面)
   - [x] pages/Login/style.module.less (样式文件)
   - [x] components/LoginForm/index.tsx (登录表单)

4. [x] 权限控制
   - [x] hooks/useAuth.ts (认证Hook)
   - [x] utils/permission.ts (权限工具)

## 4. 导航模块 [✅]
1. [x] 布局组件
   - [x] layouts/MainLayout/index.tsx (主布局)
   - [x] layouts/MainLayout/style.module.less (样式)

2. [x] 顶部导航
   - [x] components/Header/index.tsx (顶部导航)
   - [x] components/UserMenu/index.tsx (用户菜单)

3. [x] 侧边导航
   - [x] components/Sidebar/index.tsx (侧边栏)
   - [x] components/ProjectList/index.tsx (项目列表)
   - [x] components/QuickActions/index.tsx (快捷操作)

## 5. 看板模块 [✅]
1. [x] 类型定义
   - [x] types/board.ts (看板类型)
   - [x] types/task.ts (任务类型)

2. [x] API服务
   - [x] services/board.ts (看板API)
   - [x] services/task.ts (任务API)

3. [x] 状态管理
   - [x] store/slices/boardSlice.ts (看板状态)
   - [x] store/slices/taskSlice.ts (任务状态)

4. [x] 看板组件
   - [x] pages/Board/index.tsx (看板页面)
   - [x] components/Board/BoardColumn.tsx (看板列)
   - [x] components/Board/TaskCard.tsx (任务卡片)
   - [x] components/Board/BoardFilter.tsx (筛选器)

5. [x] 拖拽功能
   - [x] hooks/useDrag.ts (拖拽Hook)
   - [x] components/DragDropContext.tsx (拖拽上下文)

## 6. Backlog模块 [✅]
1. [x] 类型定义
   - [x] types/sprint.ts (Sprint类型)

2. [x] API服务
   - [x] services/sprint.ts (SprintAPI)

3. [x] 状态管理
   - [x] store/slices/sprintSlice.ts (Sprint状态)

4. [x] Backlog组件
   - [x] pages/Backlog/index.tsx (Backlog页面)
   - [x] components/Sprint/SprintList.tsx (Sprint列表)
   - [x] components/Sprint/SprintForm.tsx (Sprint表单)
   - [x] components/Backlog/TaskPool.tsx (任务池)

## 7. 组件库 [🔲]
1. [x] 通用组件
   - [x] components/TaskCard/index.tsx
   - [x] components/StatusTag/index.tsx
   - [x] components/PrioritySelect/index.tsx
   - [x] components/DatePicker/index.tsx

2. [ ] 业务组件
   - [ ] components/TaskDrawer/index.tsx
   - [ ] components/SprintModal/index.tsx
   - [ ] components/BoardConfig/index.tsx

## 8. 性能优化与测试 [🔲]
1. [ ] 性能优化
   - [ ] 实现组件懒加载
   - [ ] 优化Redux状态结构
   - [ ] 添加请求缓存

2. [ ] 测试用例
   - [ ] 编写组件测试
   - [ ] 编写Hook测试
   - [ ] 编写工具函数测试

## 9. 构建与部署 [🔲]
1. [ ] 构建配置
   - [ ] 配置生产环境变量
   - [ ] 优化构建配置
   - [ ] 配置CI/CD 