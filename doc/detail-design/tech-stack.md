# GIRA 技术栈详细说明

## 1. 开发环境要求

### 1.1 基础环境
- JDK: 17.0.9-LTS
- Node.js: 18.19.0 LTS
- npm: 10.2.3
- Git: 2.43.0
- Docker: 24.0.7
- Docker Compose: 2.23.3

### 1.2 IDE推荐
- 后端: IntelliJ IDEA 2023.3
- 前端: Visual Studio Code 1.85.1
- 数据库: DBeaver 23.3.0

## 2. 后端技术栈

### 2.1 核心框架
- Spring Boot: 3.2.1
- Spring Security: 6.2.1
- Spring Data JPA: 3.2.1
- Spring Cloud: 2023.0.0
- Spring Cloud Gateway: 4.1.1
- Spring Cloud Config: 4.1.1
- Spring Cloud Netflix: 4.1.0

### 2.2 数据库和缓存
- PostgreSQL: 14.10
- Redis: 7.2.3
- Hibernate: 6.4.1.Final
- HikariCP: 5.1.0（连接池）
- Flyway: 9.22.3（数据库版本控制）

### 2.3 安全和认证
- JWT: 0.12.3
- Spring Security OAuth2: 3.2.1
- Passay: 1.6.4（密码策略）

### 2.4 API文档
- SpringDoc OpenAPI: 2.3.0
- Swagger UI: 5.10.3

### 2.5 测试框架
- JUnit Jupiter: 5.10.1
- Mockito: 5.8.0
- Testcontainers: 1.19.3

### 2.6 监控和日志
- Spring Boot Actuator: 3.2.1
- Micrometer: 1.12.1
- Logback: 1.4.14
- SLF4J: 2.0.9

## 3. 前端技术栈

### 3.1 核心框架
- React: 18.2.0
- TypeScript: 5.3.3
- React Router: 6.21.1
- Redux Toolkit: 2.0.1
- React Query: 5.15.0

### 3.2 UI组件库
- Ant Design: 5.12.5
- Ant Design Pro Components: 2.6.48
- styled-components: 6.1.3
- TailwindCSS: 3.4.0

### 3.3 工具库
- Axios: 1.6.3
- date-fns: 3.0.6
- lodash: 4.17.21
- uuid: 9.0.1

### 3.4 开发工具
- Vite: 5.0.10
- ESLint: 8.56.0
- Prettier: 3.1.1
- Husky: 8.0.3

### 3.5 测试框架
- Jest: 29.7.0
- React Testing Library: 14.1.2
- Cypress: 13.6.1

## 4. DevOps工具链

### 4.1 容器化
- Docker: 24.0.7
- Docker Compose: 2.23.3
- Kubernetes: 1.28.4

### 4.2 CI/CD
- Jenkins: 2.426.1
- GitLab CI: 16.7.0
- SonarQube: 10.3.0

### 4.3 监控和日志
- Prometheus: 2.45.0
- Grafana: 10.2.3
- ELK Stack: 8.11.3

## 5. 开发规范和工具配置

### 5.1 代码规范
- Google Java Style Guide
- Airbnb JavaScript Style Guide
- Prettier配置
- ESLint配置
- EditorConfig

### 5.2 Git工作流
- Git Flow模型
- Commit Message规范
- Code Review流程
- 分支命名规范

### 5.3 项目结构
- 模块化设计
- 分层架构
- 微服务架构
- 领域驱动设计(DDD)

### 5.4 文档规范
- API文档规范
- 代码注释规范
- 技术文档模板
- 版本变更日志规范 