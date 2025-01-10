# GIRA 后端测试计划

## 1. 测试范围

### 1.1 功能测试
- 用户认证与授权
- 看板管理
- Sprint管理
- 任务管理
- 用户管理

### 1.2 非功能测试
- 性能测试
- 安全测试
- API 接口测试

## 2. 测试环境

### 2.1 开发环境
- JDK 21
- Spring Boot 3.2.0
- H2 Database
- Maven 3.9.6+

### 2.2 测试工具
- JUnit 5
- Spring Boot Test
- MockMvc
- H2 Database (测试数据库)

## 3. 测试计划

### 3.1 单元测试

#### 3.1.1 Service 层测试
- UserService
  - 用户创建
  - 用户查询
  - 用户更新
  - 用户删除
  - 角色管理

- BoardService
  - 看板创建
  - 看板查询
  - 看板更新
  - 看板删除
  - 列管理

- SprintService
  - Sprint创建
  - Sprint查询
  - Sprint更新
  - Sprint删除
  - 任务关联

- TaskService
  - 任务创建
  - 任务查询
  - 任务更新
  - 任务删除
  - 状态变更

#### 3.1.2 Repository 层测试
- 数据访问测试
- 关联关系测试
- 查询性能测试

### 3.2 集成测试

#### 3.2.1 API 测试
- 认证接口
  - 登录
  - 注册
  - 令牌刷新

- 用户接口
  - CRUD操作
  - 权限验证

- 看板接口
  - CRUD操作
  - 列管理
  - 权限控制

- Sprint接口
  - CRUD操作
  - 任务管理
  - 状态流转

- 任务接口
  - CRUD操作
  - 状态管理
  - 关联关系

#### 3.2.2 安全测试
- JWT认证
- 权限控制
- 接口访问控制
- 数据隔离

## 4. 测试执行

### 4.1 执行顺序
1. 单元测试
   - Service层测试
   - Repository层测试
2. 集成测试
   - API测试
   - 安全测试
3. 性能测试

### 4.2 执行方式

#### 4.2.1 自动化测试执行
使用自动化测试脚本执行测试并生成报告：
```powershell
# Windows PowerShell
cd app/gira-backend
./scripts/run-tests.ps1
```

脚本会自动：
1. 执行所有测试
2. 生成测试报告
3. 生成代码覆盖率报告
4. 更新测试结果文档

#### 4.2.2 手动执行特定测试
```bash
# 执行所有测试
mvn test

# 执行特定测试类
mvn test -Dtest=AuthControllerTest

# 执行特定测试包
mvn test -Dtest="com.rayfay.gira.api.*"
```

### 4.3 测试报告
测试执行后会生成以下报告：
1. 测试结果报告：`target/test-results/`
2. 代码覆盖率报告：`target/coverage-reports/index.html`
3. 汇总报告：`doc/test-results.md`

## 5. 测试报告

测试结果将记录在 [test-results.md](test-results.md) 文件中，包括：
- 测试执行时间
- 测试覆盖率
- 成功/失败用例统计
- 失败用例详情
- 性能测试指标 