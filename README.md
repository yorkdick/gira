# GIRA - 简化版项目管理系统

GIRA (Git-Inspired Rapid Agile) 是一个轻量级的项目管理系统，专注于看板和Backlog功能，帮助团队更好地进行任务管理和进度跟踪。

## 功能特点

- 看板管理
  - 自定义看板列
  - 任务拖拽
  - WIP限制
  - 看板统计

- Sprint管理
  - Sprint规划
  - 进度跟踪
  - 任务分配

- 任务管理
  - 任务创建和分配
  - 状态追踪
  - Backlog管理

- 用户管理
  - 基于角色的权限控制
  - 管理员和开发者角色
  - 用户信息管理

## 技术栈

### 后端技术栈
- Java 21
- Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA
- H2 Database (开发环境)
- PostgreSQL (生产环境)
- MapStruct 1.5.5.Final
- Lombok 1.18.30

## 项目文档

- [代码结构文档](app/gira-backend/doc/code-structure.md) - 详细的代码结构说明，包括架构设计、模块说明和开发指南
- [数据库设计文档](doc/design/basic-design/database.md) - 数据库表结构和关系说明
- [API文档](http://localhost:8080/swagger-ui/index.html) - API接口文档（需要启动服务后访问）
- [需求设计文档](doc/design/basic-design/requirements.md)
- [功能设计文档](doc/design/basic-design/functional.md)
- [后台详细设计](doc/design/detail-design/backend-design.md)
- [后台开发指南](doc/design/detail-design/backend-development.md)
- [前端详细设计](doc/design/detail-design/frontend-design.md)
- [前端开发计划](doc/design/detail-design/frontend-development-plan.md)
- [前端开发指南](doc/design/detail-design/frontend-development.md)
- [前端任务列表](doc/design/detail-design/frontend-task-list.md)
- [测试计划](app/gira-backend/doc/test-plan.md) - 详细的测试计划和执行指南
- [测试结果](app/gira-backend/doc/test-results.md) - 测试执行结果和分析报告

## 快速开始

### 环境要求
- OpenJDK 21
- Maven 3.9.6+
- PostgreSQL 14+ (生产环境)

### 开发环境设置

1. 克隆项目
```bash
git clone https://github.com/rayfay/gira.git
cd gira
```

2. 编译项目
```bash
cd app/gira-backend
mvn clean install
```

3. 运行项目（开发模式）
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

开发环境默认使用H2数据库，无需额外配置。

## 项目结构

```
gira/
├── app/
│   └── gira-backend/        # 后端应用
│       ├── src/             # 源代码
│       │   ├── main/        # 主要代码
│       │   └── test/        # 测试代码
│       └── doc/             # 后端文档
└── doc/                     # 项目文档
    ├── design/              # 设计文档
    │   ├── basic-design/    # 基础设计
    │   └── detail-design/   # 详细设计
    └── assets/              # 文档资源
```

## 开发规范

### 代码提交规范

```
<type>(<scope>): <subject>

<body>

<footer>
```

Type类型:
- feat: 新功能
- fix: 修复
- docs: 文档
- style: 格式
- refactor: 重构
- test: 测试
- chore: 构建过程或辅助工具的变动

### API文档

- 开发环境: http://localhost:8080/swagger-ui/index.html
- API文档: http://localhost:8080/v3/api-docs

## 测试

```bash
cd app/gira-backend
mvn test
```

## 许可证

[MIT License](LICENSE) 