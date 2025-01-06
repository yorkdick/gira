# GIRA 测试设计文档

## 1. 测试策略

### 1.1 测试级别
- 集成测试（Integration Test）

### 1.2 测试工具
- JUnit 5：单元测试框架

### 1.3 测试环境
- 开发环境（Development）

## 2. 单元测试用例

### 2.0 测试基础类
#### 2.0.1 BaseApiTest
- 提供基础的测试配置和工具
- 包含通用的测试常量和工具方法
- 初始化 RestTemplate 和基础 URL

#### 2.0.2 AuthenticatedApiTest
```java
public class AuthenticatedApiTest extends BaseApiTest {
    // 创建带认证头的 HttpHeaders
    protected HttpHeaders getAuthHeaders()
    
    // 创建带认证的 HttpEntity（无请求体）
    protected HttpEntity<?> createAuthEntity()
    
    // 创建带认证的 HttpEntity（带请求体）
    protected <T> HttpEntity<T> createAuthEntity(T body)
    
    // 创建带认证和 JSON 内容类型的 HttpEntity（无请求体）
    protected HttpEntity<?> createJsonAuthEntity()
    
    // 创建带认证和 JSON 内容类型的 HttpEntity（带请求体）
    protected <T> HttpEntity<T> createJsonAuthEntity(T body)
}
```

### 2.1 用户认证模块

#### 2.1.1 用户登录测试
```java
@Test
void testLogin() {
    // 测试成功登录
    testSuccessfulLogin();
    
    // 测试用户名不存在
    testLoginWithNonexistentUsername();
    
    // 测试密码错误
    testLoginWithWrongPassword();
    
    // 测试账户被锁定
    testLoginWithLockedAccount();
    
    // 测试记住密码功能
    testLoginWithRememberMe();
}
```

#### 2.1.2 用户注册测试
```java
@Test
void testRegister() {
    // 测试成功注册
    testSuccessfulRegistration();
    
    // 测试用户名已存在
    testRegisterWithExistingUsername();
    
    // 测试邮箱已存在
    testRegisterWithExistingEmail();
    
    // 测试密码强度验证
    testRegisterWithWeakPassword();
}
```

### 2.2 Backlog模块

#### 2.2.1 需求管理测试
```java
@Test
void testIssueManagement() {
    // 测试创建需求
    testCreateIssue();
    
    // 测试更新需求
    testUpdateIssue();
    
    // 测试删除需求
    testDeleteIssue();
    
    // 测试需求状态变更
    testChangeIssueStatus();
}
```

#### 2.2.2 需求查询测试
```java
@Test
void testIssueQuery() {
    // 测试分页查询
    testIssuesPagination();
    
    // 测试条件过滤
    testIssuesFilter();
    
    // 测试排序功能
    testIssuesSort();
}
```

### 2.3 看板模块

#### 2.3.1 看板管理测试
```java
@Test
void testBoardManagement() {
    // 测试创建看板
    testCreateBoard();
    
    // 测试配置看板列
    testConfigureBoardColumns();
    
    // 测试任务拖拽
    testDragAndDropTask();
}
```

## 3. 集成测试用例

### 3.1 用户认证流程
```java
@Test
void testAuthenticationFlow() {
    // 测试完整认证流程
    String token = testRegisterAndLogin();
    
    // 测试Token验证
    testTokenValidation(token);
    
    // 测试Token刷新
    testTokenRefresh(token);
    
    // 测试登出
    testLogout(token);
}
```

### 3.2 需求管理流程
```java
@Test
void testIssueWorkflow() {
    // 测试需求创建到完成的完整流程
    Long issueId = testCreateIssueWithDetails();
    
    // 测试状态流转
    testIssueStatusTransition(issueId);
    
    // 测试评论功能
    testIssueComments(issueId);
    
    // 测试附件上传
    testIssueAttachments(issueId);
}
```

## 4. 系统测试用例

### 4.1 功能测试
1. 用户管理测试
   - 验证用户注册、登录、注销流程
   - 验证用户信息修改
   - 验证权限控制

2. 项目管理测试
   - 验证项目创建、配置流程
   - 验证团队协作功能
   - 验证项目角色权限

3. 需求管理测试
   - 验证需求创建、编辑、删除
   - 验证需求状态流转
   - 验证需求关联功能

4. 看板功能测试
   - 验证看板视图切换
   - 验证任务拖拽功能
   - 验证工作流规则