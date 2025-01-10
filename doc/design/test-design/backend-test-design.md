# GIRA后台测试设计文档

## 1. 测试目的

- 验证后台API接口的功能正确性
- 确保业务逻辑符合需求规范
- 验证数据访问和处理的准确性
- 确保权限控制的有效性
- 验证异常处理的合理性

## 2. 测试工具

- JUnit 5：单元测试框架
- Mockito：模拟对象框架
- Spring Test：Spring集成测试支持
- MockMvc：API接口测试工具
- H2 Database：测试数据库

## 3. 测试方法

### 3.1 单元测试
- 对Service层的业务逻辑进行单元测试
- 使用Mockito模拟依赖对象
- 验证方法的输入输出
- 测试异常处理逻辑

### 3.2 集成测试
- 使用@SpringBootTest进行集成测试
- 验证API接口的功能
- 测试数据库操作
- 验证权限控制

### 3.3 测试数据管理
- 使用H2内存数据库
- 通过SQL脚本初始化测试数据
- 每个测试用例执行后清理数据

## 4. 测试用例

### 4.1 认证模块测试用例

| 测试功能 | 测试场景 | 输入 | 预期输出 | 验证点 |
|---------|---------|------|----------|--------|
| 用户登录 | 正确的用户名密码 | {"username": "test", "password": "password"} | {"token": "xxx", "tokenType": "Bearer", "expiresIn": 3600} | HTTP 200, token不为空 |
| 用户登录 | 错误的密码 | {"username": "test", "password": "wrong"} | {"error": "用户名或密码错误"} | HTTP 401 |
| 用户登录 | 用户名不存在 | {"username": "notexist", "password": "test"} | {"error": "用户名或密码错误"} | HTTP 401 |
| 刷新令牌 | 有效的令牌 | Header: {"Authorization": "Bearer xxx"} | {"token": "xxx", "tokenType": "Bearer", "expiresIn": 3600} | HTTP 200, 新token |
| 刷新令牌 | 过期的令牌 | Header: {"Authorization": "Bearer expired"} | {"error": "令牌已过期"} | HTTP 401 |
| 令牌验证 | 有效令牌访问API | Header: {"Authorization": "Bearer xxx"} | 正常响应 | HTTP 200 |
| 令牌验证 | 无效令牌访问API | Header: {"Authorization": "Bearer invalid"} | {"error": "无效的令牌"} | HTTP 401 |
| 令牌验证 | 令牌过期访问API | Header: {"Authorization": "Bearer expired"} | {"error": "令牌已过期"} | HTTP 401 |

### 4.2 用户模块测试用例

| 测试功能 | 测试场景 | 输入 | 预期输出 | 验证点 |
|---------|---------|------|----------|--------|
| 创建用户 | 管理员创建用户 | {"username": "new", "password": "pass", "email": "test@test.com"} | {"id": 1, "username": "new", "email": "test@test.com"} | HTTP 201 |
| 创建用户 | 普通用户创建 | 同上 | {"error": "无权限"} | HTTP 403 |
| 创建用户 | 用户名已存在 | 同上 | {"error": "用户名已存在"} | HTTP 409 |
| 更新用户 | 更新自己信息 | {"email": "new@test.com"} | {"id": 1, "email": "new@test.com"} | HTTP 200 |
| 更新用户 | 更新他人信息 | 同上 | {"error": "无权限"} | HTTP 403 |
| 修改密码 | 正确的原密码 | {"oldPassword": "old", "newPassword": "new"} | 无返回内容 | HTTP 200 |
| 删除用户 | 管理员删除用户 | DELETE /api/users/1 | 无返回内容 | HTTP 200 |
| 删除用户 | 普通用户删除 | 同上 | {"error": "无权限"} | HTTP 403 |
| 获取用户列表 | 分页查询 | GET /api/users?page=0&size=10 | {"content": [...], "totalElements": 100} | HTTP 200 |
| 获取用户列表 | 排序查询 | GET /api/users?sort=username,desc | {"content": [...]} | HTTP 200 |

### 4.3 看板模块测试用例

| 测试功能 | 测试场景 | 输入 | 预期输出 | 验证点 |
|---------|---------|------|----------|--------|
| 创建看板 | 管理员创建 | {"name": "测试看板", "columns": [{"name": "待处理"}]} | {"id": 1, "name": "测试看板"} | HTTP 201 |
| 更新列 | 添加新列 | {"columns": [{"name": "进行中"}]} | {"id": 1, "columns": [...]} | HTTP 200 |
| 更新列 | 删除有任务的列 | {"columns": []} | {"error": "列中存在任务"} | HTTP 400 |
| 更新列 | 设置WIP限制 | {"columns": [{"id": 1, "wipLimit": 5}]} | {"id": 1, "columns": [...]} | HTTP 200 |
| 归档看板 | 正常归档 | PUT /api/boards/1/archive | 无返回内容 | HTTP 200 |
| 归档看板 | 已归档的看板 | PUT /api/boards/1/archive | {"error": "看板已归档"} | HTTP 400 |
| 归档看板 | 有活动Sprint | PUT /api/boards/1/archive | {"error": "存在活动Sprint"} | HTTP 400 |
| 获取任务 | 按列筛选 | GET /api/boards/1/tasks?columnId=1 | [{"id": 1, "title": "任务1"}] | HTTP 200 |
| 获取看板列表 | 状态筛选 | GET /api/boards?status=ACTIVE | [{"id": 1, "status": "ACTIVE"}] | HTTP 200 |
| 获取看板列表 | 分页查询 | GET /api/boards?page=0&size=10 | {"content": [...], "totalElements": 50} | HTTP 200 |

### 4.4 Sprint模块测试用例

| 测试功能 | 测试场景 | 输入 | 预期输出 | 验证点 |
|---------|---------|------|----------|--------|
| 创建Sprint | 正常创建 | {"name": "Sprint 1", "startDate": "2024-01-01", "endDate": "2024-01-14"} | {"id": 1, "name": "Sprint 1", "status": "PLANNING"} | HTTP 200 |
| 创建Sprint | 已有活动Sprint | 同上 | {"error": "已存在活动中的Sprint"} | HTTP 400 |
| 创建Sprint | 开始日期晚于结束日期 | {"startDate": "2024-02-01", "endDate": "2024-01-01"} | {"error": "开始日期不能晚于结束日期"} | HTTP 400 |
| 创建Sprint | 开始日期早于今天 | {"startDate": "2024-01-01", "endDate": "2024-01-14"} | {"error": "开始日期不能早于今天"} | HTTP 400 |
| 开始Sprint | 正常开始 | PUT /api/sprints/{id}/start | {"id": 1, "status": "ACTIVE"} | HTTP 200 |
| 开始Sprint | 已有活动Sprint | PUT /api/sprints/{id}/start | {"error": "已存在活动中的Sprint"} | HTTP 400 |
| 开始Sprint | 无权限 | PUT /api/sprints/{id}/start | {"error": "无权限操作"} | HTTP 403 |
| 完成Sprint | 正常完成 | PUT /api/sprints/{id}/complete | {"id": 1, "status": "COMPLETED"} | HTTP 200 |
| 完成Sprint | 非活动状态 | PUT /api/sprints/{id}/complete | {"error": "只能完成活动中的Sprint"} | HTTP 400 |
| 完成Sprint | 无权限 | PUT /api/sprints/{id}/complete | {"error": "无权限操作"} | HTTP 403 |
| 更新Sprint | 修改计划中Sprint | {"name": "Sprint 2"} | {"id": 1, "name": "Sprint 2"} | HTTP 200 |
| 更新Sprint | 无权限 | {"name": "Sprint 2"} | {"error": "无权限操作"} | HTTP 403 |
| 获取Sprint | 有效ID | GET /api/sprints/{id} | {"id": 1, "name": "Sprint 1"} | HTTP 200 |
| 获取Sprint | 无效ID | GET /api/sprints/999 | {"error": "Sprint不存在"} | HTTP 404 |
| 获取看板Sprints | 正常查询 | GET /api/boards/{boardId}/sprints | {"content": [...], "totalElements": 10} | HTTP 200 |

### 4.5 任务模块测试用例

| 测试功能 | 测试场景 | 输入 | 预期输出 | 验证点 |
|---------|---------|------|----------|--------|
| 创建任务 | 正常创建 | {"title": "任务1", "description": "描述", "boardId": 1, "columnId": 1} | {"id": 1, "title": "任务1", "status": "TODO"} | HTTP 200 |
| 创建任务 | 缺少标题 | {"description": "描述", "boardId": 1, "columnId": 1} | {"error": "任务标题不能为空"} | HTTP 400 |
| 创建任务 | 无效的看板 | {"title": "任务1", "boardId": 999, "columnId": 1} | {"error": "看板不存在"} | HTTP 400 |
| 创建任务 | 指定经办人 | {"title": "任务1", "assigneeId": 1, "boardId": 1, "columnId": 1} | {"id": 1, "assignee": {"id": 1}} | HTTP 200 |
| 查询任务 | 有效ID | GET /api/tasks/1 | {"id": 1, "title": "任务1"} | HTTP 200 |
| 查询任务 | 无效ID | GET /api/tasks/999 | {"error": "任务不存在"} | HTTP 404 |
| 查询任务列表 | 按看板查询 | GET /api/tasks/boards/1 | {"content": [...], "totalElements": 10} | HTTP 200 |
| 查询任务列表 | 按经办人查询 | GET /api/tasks/assignee/1 | {"content": [...]} | HTTP 200 |
| 查询任务列表 | 按状态和优先级 | GET /api/tasks?status=TODO&priority=MEDIUM | {"content": [...]} | HTTP 200 |
| 更新状态 | TODO到IN_PROGRESS | {"status": "IN_PROGRESS"} | {"id": 1, "status": "IN_PROGRESS"} | HTTP 200 |
| 更新状态 | IN_PROGRESS到DONE | {"status": "DONE"} | {"id": 1, "status": "DONE"} | HTTP 200 |
| 更新状态 | 无效状态变更 | {"status": "DONE"} | {"error": "无效的状态变更"} | HTTP 400 |
| 删除任务 | 管理员删除 | DELETE /api/tasks/1 | 无返回内容 | HTTP 200 |
| 删除任务 | 无权限删除 | DELETE /api/tasks/1 | {"error": "无权限操作"} | HTTP 403 |

## 5. 测试覆盖率要求

### 5.1 代码覆盖率
- 单元测试覆盖率 > 80%
- 分支覆盖率 > 70%
- 关键业务逻辑覆盖率 100%

### 5.2 功能覆盖率
- API接口测试覆盖率 100%
- 权限验证测试覆盖率 100%
- 异常处理测试覆盖率 > 90%

## 6. 测试执行

### 6.1 测试环境
- 开发环境：使用H2内存数据库
- 测试环境：使用独立的PostgreSQL数据库
- CI环境：使用Docker容器化的数据库

### 6.2 测试流程
1. 单元测试作为开发流程的一部分
2. 提交代码前运行所有测试
3. CI流程中自动运行测试套件
4. 定期进行全量回归测试

### 6.3 测试报告
- 测试执行结果统计
- 代码覆盖率报告
- 测试失败分析
- 性能指标统计 