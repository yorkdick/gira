# GIRA UI设计文档

## 1. 设计规范

### 1.1 设计原则
- 简洁直观：界面清晰，操作简单
- 一致性：视觉和交互保持一致
- 响应式：适配不同设备尺寸
- 可访问性：支持键盘操作和屏幕阅读

### 1.2 色彩规范
- 主色调：#1890ff（蓝色）
- 辅助色：
  - 成功：#52c41a
  - 警告：#faad14
  - 错误：#f5222d
  - 链接：#1890ff
- 中性色：
  - 标题：#262626
  - 正文：#595959
  - 辅助文字：#8c8c8c
  - 边框：#d9d9d9
  - 背景：#f0f2f5

### 1.3 字体规范
- 主要字体：
  - 中文：-apple-system, "PingFang SC"
  - 英文：Roboto, Arial
- 字号：
  - 标题：20px/24px
  - 副标题：16px/22px
  - 正文：14px/22px
  - 辅助文字：12px/20px

### 1.4 间距规范
- 基础间距：8px
- 内边距：16px/24px
- 外边距：16px/24px
- 组件间距：16px/24px

## 2. 页面布局

### 2.1 整体布局
```
+------------------+------------------+
|      Header      |      Header      |
+--------+---------+------------------+
|        |                           |
|        |                           |
|  侧边栏 |          内容区           |
|        |                           |
|        |                           |
+--------+---------------------------+
```

#### 2.1.1 Header区域
- Logo
- 全局搜索
- 导航菜单
- 用户信息
- 通知中心

#### 2.1.2 侧边栏
- 项目切换
- 主导航菜单
- 快捷操作

#### 2.1.3 内容区
- 面包屑导航
- 页面标题
- 主要内容
- 操作按钮

## 3. 页面设计

### 3.1 登录页面
- 布局：居中卡片式设计
- 组件：
  - 登录表单
  - 记住密码选项
  - 忘记密码链接
  - 注册入口
- 交互：
  - 表单验证
  - 登录状态提示
  - 自动跳转

### 3.2 Backlog页面
- 布局：双栏布局
- 左侧：
  - 需求列表
  - 筛选器
  - 排序选项
- 右侧：
  - 需求详情
  - 评论区
- 交互：
  - 拖拽排序
  - 快速编辑
  - 状态切换

### 3.3 看板页面
- 布局：横向滚动布局
- 组件：
  - 状态列
  - 任务卡片
  - 快速添加
- 交互：
  - 拖拽移动
  - 列展开/收起
  - 任务筛选

### 3.4 设置页面
- 布局：左侧导航 + 右侧内容
- 分类：
  - 个人设置
  - 项目设置
  - 团队管理
  - 权限配置

## 4. 组件设计

### 4.1 通用组件

#### 4.1.1 导航组件
```jsx
<Menu theme="dark" mode="horizontal">
  <Menu.Item key="backlog">Backlog</Menu.Item>
  <Menu.Item key="board">看板</Menu.Item>
  <Menu.Item key="reports">报表</Menu.Item>
</Menu>
```

#### 4.1.2 表单组件
```jsx
<Form layout="vertical">
  <Form.Item label="标题" required>
    <Input placeholder="请输入标题" />
  </Form.Item>
  <Form.Item label="描述">
    <Input.TextArea rows={4} />
  </Form.Item>
</Form>
```

#### 4.1.3 任务卡片
```jsx
<Card hoverable>
  <Card.Meta
    title="任务标题"
    description="任务描述"
  />
  <div className="card-footer">
    <Avatar src="user.png" />
    <Tag color="blue">进行中</Tag>
  </div>
</Card>
```

### 4.2 业务组件

#### 4.2.1 需求列表项
```jsx
<List.Item
  actions={[<EditButton />, <DeleteButton />]}
  extra={<Priority level={3} />}
>
  <List.Item.Meta
    avatar={<IssueTypeIcon type="story" />}
    title={<a href="#">需求标题</a>}
    description="创建时间：2024-01-01"
  />
</List.Item>
```

#### 4.2.2 看板列
```jsx
<BoardColumn
  title="进行中"
  count={5}
  limit={10}
>
  <TaskCard />
  <TaskCard />
  <AddCard />
</BoardColumn>
```

## 5. 交互设计

### 5.1 拖拽交互
- 需求优先级调整
- 看板任务移动
- 附件上传

### 5.2 状态转换
- 任务状态流转
- 审批流程
- 权限变更

### 5.3 实时更新
- WebSocket 通知
- 协同编辑
- 状态同步

## 6. 响应式设计

### 6.1 断点设计
- xs: < 576px
- sm: ≥ 576px
- md: ≥ 768px
- lg: ≥ 992px
- xl: ≥ 1200px
- xxl: ≥ 1600px

### 6.2 适配策略
- 移动端：
  - 隐藏侧边栏
  - 简化导航
  - 调整布局
- 平板：
  - 压缩侧边栏
  - 优化表单
- 桌面端：
  - 完整功能
  - 多列布局

## 7. 动效设计

### 7.1 过渡动画
- 页面切换：fade
- 弹窗：zoom
- 抽屉：slide

### 7.2 交互反馈
- 按钮点击：缩放
- 卡片悬浮：阴影
- 拖拽：跟随

### 7.3 加载状态
- 全局loading
- 局部skeleton
- 进度指示器 