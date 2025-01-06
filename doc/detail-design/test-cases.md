# GIRA 系统测试用例文档

## 1. 用户认证模块测试用例

### TC-001 登录测试
1. TC-001-1: 成功登录
   - 输入：
     ```json
     {
       "username": "admin",
       "password": "Password123!",
       "rememberMe": false
     }
     ```
   - 预期：
     - 状态码：200
     - 响应体：包含 token 和用户信息

2. TC-001-2: 用户名不存在
   - 输入：
     ```json
     {
       "username": "nonexistent",
       "password": "password123"
     }
     ```
   - 预期：
     - 状态码：404
     - 响应体：`{"message": "用户不存在"}`

3. TC-001-3: 密码错误
   - 输入：
     ```json
     {
       "username": "admin",
       "password": "wrongpassword"
     }
     ```
   - 预期：
     - 状态码：401
     - 响应体：`{"message": "密码错误"}`

4. TC-001-4: 账户被锁定
   - 输入：
     ```json
     {
       "username": "locked_user",
       "password": "password123"
     }
     ```
   - 预期：
     - 状态码：404
     - 响应体：`{"message": "账户已被锁定"}`

5. TC-001-5: 无效的请求格式
   - 输入：
     ```json
     {
       "username": "",
       "password": ""
     }
     ```
   - 预期：
     - 状态码：400
     - 响应体：`{"message": "用户名和密码不能为空"}`

### TC-002 Token认证测试
1. TC-002-1: 有效Token访问
   - 输入：
     - Headers: `Authorization: Bearer <valid_token>`
     - GET /api/users/me
   - 预期：
     - 状态码：200
     - 返回用户信息

2. TC-002-2: 无效Token
   - 输入：
     - Headers: `Authorization: Bearer invalid_token`
     - GET /api/users/me
   - 预期：
     - 状态码：401
     - 响应体：`{"message": "无效的Token"}`

3. TC-002-3: Token刷新
   - 输入：
     - Headers: `Authorization: Bearer <valid_token>`
     - POST /api/auth/refresh
   - 预期：
     - 状态码：200
     - 返回新的Token

### TC-003 用户管理测试
1. TC-003-1: 创建用户
   - 输入：
     ```json
     {
       "username": "newuser",
       "email": "newuser@gira.com",
       "password": "Password123!",
       "fullName": "New User",
       "role": "USER",
       "enabled": true
     }
     ```
   - 预期：
     - 状态码：201
     - 响应体：包含用户ID和基本信息

2. TC-003-2: 创建用户 - 用户名已存在
   - 输入：
     ```json
     {
       "username": "admin",
       "email": "newuser@gira.com",
       "password": "Password123!"
     }
     ```
   - 预期：
     - 状态码：400
     - 响应体：`{"message": "用户名已存在"}`

3. TC-003-3: 获取用户详情
   - 输入：
     - GET /api/users/{userId}
   - 预期：
     - 状态码：200
     - 返回用户详细信息

4. TC-003-4: 更新用户
   - 输入：
     ```json
     {
       "fullName": "Updated User",
       "avatar": "https://example.com/avatar.jpg"
     }
     ```
   - 预期：
     - 状态码：200
     - 响应体：包含更新后的信息

5. TC-003-5: 删除用户
   - 输入：
     - DELETE /api/users/{userId}
   - 预期：
     - 状态码：200
     - 用户被成功删除

6. TC-003-6: 获取用户列表
   - 输入：
     - GET /api/users?page=0&size=10
   - 预期：
     - 状态码：200
     - 返回分页的用户列表

7. TC-003-7: 修改用户密码
   - 输入：
     ```json
     {
       "oldPassword": "1qaz@WSX",
       "newPassword": "NewPassword123!"
     }
     ```
   - 预期：
     - 状态码：200
     - 密码修改成功
   - 后置处理：
     - 使用新密码登录验证
     - 恢复原密码以确保其他测试用例正常执行
   - 注意事项：
     - 此测试用例会临时修改管理员密码
     - 使用 try-finally 确保密码总是被恢复
     - 验证新密码可用于登录
     - 确保密码恢复成功

8. TC-003-8: 禁用用户
   - 输入：
     - PUT /api/users/{userId}/disable
   - 预期：
     - 状态码：200
     - 用户状态更新为禁用

9. TC-003-9: 搜索用户
   - 输入：
     - GET /api/users/search?keyword=admin
   - 预期：
     - 状态码：200
     - 返回匹配的用户列表

## 2. 项目管理模块测试用例

### TC-003 项目管理测试
1. TC-003-1: 创建项目
   - 输入：
     ```json
     {
       "name": "新项目",
       "description": "新项目描述"
     }
     ```
   - 预期：返回200，创建成功

2. TC-003-2: 项目名称重复
   - 输入：
     ```json
     {
       "name": "已存在的项目",
       "description": "描述"
     }
     ```
   - 预期：返回400，提示"项目名称已存在"

3. TC-003-3: 无权限创建
   - 输入：同TC-003-1
   - 预期：返回403，提示"无权限创建项目"

4. TC-003-4: 删除项目
   - 输入：项目ID=1
   - 预期：返回200，删除成功

5. TC-003-5: 无权限删除项目
   - 输入：项目ID=1
   - 预期：返回403，提示"无权限删除项目"

6. TC-003-6: 删除不存在的项目
   - 输入：项目ID=999
   - 预期：返回404，提示"项目不存在"

## 3. 看板管理模块测试用例

### TC-004 看板管理测试
1. TC-004-1: 创建看板
   - 输入：
     ```json
     {
       "name": "开发看板",
       "description": "描述",
       "projectId": 1,
       "columns": [
         {"name": "待处理", "position": 0},
         {"name": "进行中", "position": 1},
         {"name": "已完成", "position": 2}
       ]
     }
     ```
   - 预期：返回201，创建成功

2. TC-004-2: 更新看板
   - 输入：
     ```json
     {
       "name": "新名称",
       "description": "新描述"
     }
     ```
   - 预期：返回200，更新成功

3. TC-004-3: 移动任务
   - 输入：
     ```json
     {
       "taskId": 1,
       "columnId": 2,
       "position": 0
     }
     ```
   - 预期：返回200，移动成功

4. TC-004-4: 删除看板
   - 输入：看板ID=1
   - 预期：返回200，删除成功

5. TC-004-5: 删除看板列
   - 输入：看板ID=1, 列ID=1
   - 预期：返回200，删除成功

6. TC-004-6: 无权限删除看板
   - 输入：看板ID=1
   - 预期：返回403，提示"无权限删除看板"

7. TC-004-7: 删除不存在的看板
   - 输入：看板ID=999
   - 预期：返回404，提示"看板不存在"

8. TC-004-8: 删除最后一列
   - 输入：看板ID=1, 列ID=1
   - 预期：返回400，提示"看板必须至少保留一列"

## 4. 任务管理模块测试用例

### TC-005 任务管理测试
1. TC-005-1: 创建任务
   - 输入：
     ```json
     {
       "title": "测试任务",
       "description": "任务描述",
       "type": "STORY",
       "priority": "HIGH",
       "dueDate": "2024-01-01T00:00:00",
       "columnId": 1,
       "assigneeId": 1
     }
     ```
   - 预期：返回201，创建成功

2. TC-005-2: 更新任务状态
   - 输入：
     ```json
     {
       "status": "IN_PROGRESS"
     }
     ```
   - 预期：返回200，更新成功

3. TC-005-3: 分配任务
   - 输入：任务ID=1, 用户ID=2
   - 预期：返回200，分配成功

4. TC-005-4: 删除任务
   - 输入：任务ID=1
   - 预期：返回200，删除成功

5. TC-005-5: 无权限删除任务
   - 输入：任务ID=1
   - 预期：返回403，提示"无权限删除任务"

6. TC-005-6: 删除不存在的任务
   - 输入：任务ID=999
   - 预期：返回404，提示"任务不存在"

7. TC-005-7: 删除有评论的任务
   - 输入：任务ID=1（包含评论）
   - 预期：返回200，删除成功，级联删除评论

8. TC-005-8: 删除有附件的任务
   - 输入：任务ID=1（包含附件）
   - 预期：返回200，删除成功，级联删除附件

## 5. 测试数据准备

### 5.1 初始化数据
```sql
-- 用户数据
INSERT INTO users (id, username, password, email, status) VALUES
(1, 'user1', '{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'user1@gira.com', 1),
(2, 'locked_user', '{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'locked@gira.com', 0);

-- 项目数据
INSERT INTO teams (id, name, description, created_by) VALUES
(1, '已存在的项目', '这是一个测试项目', 1);

-- 看板数据
INSERT INTO boards (id, name, description, project_id) VALUES
(1, '测试看板', '这是一个测试看板', 1);

-- 看板列数据
INSERT INTO board_columns (id, name, position, board_id) VALUES
(1, '待处理', 0, 1),
(2, '进行中', 1, 1),
(3, '已完成', 2, 1);

-- 任务数据
INSERT INTO tasks (id, title, description, type, priority, column_id, assignee_id) VALUES
(1, '测试任务', '这是一个测试任务', 'STORY', 'HIGH', 1, 1);

-- 任务评论数据
INSERT INTO comments (id, content, task_id, user_id) VALUES
(1, '这是一个测试评论', 1, 1);

-- 任务附件数据
INSERT INTO attachments (id, filename, content_type, size, path, task_id, user_id) VALUES
(1, 'test.txt', 'text/plain', 1024, '/uploads/test.txt', 1, 1);
```

### 5.2 清理数据
```sql
DELETE FROM attachments;
DELETE FROM comments;
DELETE FROM tasks;
DELETE FROM board_columns;
DELETE FROM boards;
DELETE FROM teams;
DELETE FROM users;
```

## 6. 测试环境要求

1. 数据库
   - PostgreSQL 14+
   - 创建测试数据库：gira_test

2. 应用配置
   - 测试配置文件：application-test.yml
   - 使用H2内存数据库进行单元测试
   - JWT密钥配置：jwt.secret=test_secret_key

3. 测试工具
   - JUnit 5
   - Spring Boot Test
   - MockMvc
   - H2 Database

## 7. 测试执行顺序

### 1. 认证测试（AuthApiTest）
```java
1.1 登录测试系列
- testSuccessfulLogin()
- testLoginWithNonexistentUsername()
- testLoginWithWrongPassword()
- testLoginWithLockedAccount()
- testLoginWithInvalidFormat()

1.2 注册测试系列
- testSuccessfulRegistration()
- testRegisterWithExistingEmail()
- testRegisterWithWeakPassword()
- testRegisterWithoutAuth()

1.3 Token测试系列
- testValidTokenAccess()
- testInvalidToken()
- testTokenRefresh()
```

### 2. 用户管理测试（UserApiTest）
```java
2.1 用户信息测试
- testGetCurrentUser()
- testGetUserById()
- testGetUserList()

2.2 用户操作测试
- testCreateUser()
- testUpdateUser()
- testDeleteUser()

2.3 用户权限测试
- testUserPermissions()
```

### 3. 项目管理测试（ProjectApiTest）
```java
3.1 项目基础操作
- testCreateProject()
- testGetProjectById()
- testGetProjectList()
- testUpdateProject()
- testDeleteProject()

3.2 项目成员管理
- testAddProjectMember()
- testRemoveProjectMember()

3.3 项目设置
- testUpdateProjectSettings()
```

### 4. 看板测试（BoardApiTest）
```java
4.1 看板操作
- testCreateBoard()
- testGetBoardById()
- testUpdateBoard()
- testDeleteBoard()

4.2 看板列管理
- testAddBoardColumn()
- testUpdateColumnOrder()
```

### 5. 待办事项测试（BacklogApiTest）
```java
5.1 待办事项管理
- testCreateBacklogItem()
- testGetBacklogItems()
- testUpdateBacklogItem()
- testDeleteBacklogItem()

5.2 优先级管理
- testUpdateItemPriority()
```

### 6. 任务测试（TaskApiTest）
```java
6.1 任务基础操作
- testCreateTask()
- testGetTaskById()
- testUpdateTask()
- testDeleteTask()

6.2 任务状态管理
- testUpdateTaskStatus()
- testAssignTask()

6.3 任务关联
- testAddSubtask()
- testLinkRelatedTasks()
```

### 7. 冲刺测试（SprintApiTest）
```java
7.1 冲刺管理
- testCreateSprint()
- testGetSprintById()
- testUpdateSprint()
- testDeleteSprint()

7.2 冲刺操作
- testStartSprint()
- testCompleteSprint()
```

### 8. 评论测试（CommentApiTest）
```java
8.1 评论管理
- testAddComment()
- testGetComments()
- testUpdateComment()
- testDeleteComment()
```

测试执行注意事项：
1. 每个测试类都继承自 `BaseApiTest`，确保有正确的认证token
2. 测试执行顺序遵循业务依赖关系
3. 每个测试用例都应该是独立的，不依赖其他测试的执行结果
4. 测试数据应该在测试前准备好，测试后清理
5. 所有测试都应该验证HTTP状态码和响应内容

执行测试的方式：
```bash
# 执行所有测试
mvn test

# 执行特定测试类
mvn test -Dtest=AuthApiTest

# 执行特定测试方法
mvn test -Dtest=AuthApiTest#testSuccessfulLogin