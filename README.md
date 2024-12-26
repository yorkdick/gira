# GIRA - 项目管理系统

GIRA是一个轻量级的项目管理系统，提供类似JIRA的核心功能，包括Backlog管理和看板功能。

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.1
- Spring Security 6.2.1
- PostgreSQL 14.10
- Redis 7.2.3

### 前端
- React 18.2.0
- TypeScript 5.3.3
- Ant Design 5.12.5
- TailwindCSS 3.4.0

## 安装说明

详细的安装和部署说明请参考 [安装文档](app/INSTALL.md)。

## 项目结构

```
gira/
├── app/                                # 应用目录
│   ├── gira-backend/                  # 后端项目
│   ├── gira-frontend/                 # 前端项目
│   └── install/                       # 安装部署相关
│       ├── docker/                    # Docker配置
│       │   ├── postgres/             # PostgreSQL配置
│       │   ├── docker-compose.yml    # 开发环境配置
│       │   └── docker-compose.prod.yml # 生产环境配置
│
├── doc/                                # 文档目录
│   ├── basic-design/                   # 基础设计文档
│   │   ├── architecture.md            # 系统架构设计
│   │   ├── api.md                     # API接口设计
│   │   ├── functional.md              # 功能设计
│   │   └── ui.md                      # UI设计
│   │
│   └── detail-design/                  # 详细设计文档
│       ├── tech-stack.md              # 技术栈详细说明
│       ├── backend-design.md          # 后端详细设计
│       ├── frontend-design.md         # 前端详细设计
│       ├── database-design.md         # 数据库详细设计
│       └── business-logic.md          # 业务逻辑详细设计
│
├── scripts/                            # 脚本目录
│   └── database/                      # 数据库脚本
│
├── .gitignore                         # Git忽略文件
└── README.md                          # 项目说明文档
```

## 文档说明

### 基础设计文档 (doc/basic-design/)
- `architecture.md`: 系统整体架构设计，包含技术选型、系统模块划分等
- `api.md`: API接口设计，包含接口规范、认证方式、错误码等
- `functional.md`: 功能设计，详细描述系统的功能模块和业务流程
- `ui.md`: UI设计，包含界面布局、组件设计、交互流程等

### 详细设计文档 (doc/detail-design/)
- `tech-stack.md`: 技术栈详细说明，包含版本、配置和使用说明
- `backend-design.md`: 后端详细设计，包含代码结构、核心服务等
- `frontend-design.md`: 前端详细设计，包含组件设计、状态管理等
- `database-design.md`: 数据库设计，包含表结构、索引、优化等
- `business-logic.md`: 业务逻辑设计，包含业务流程、规则等

## 后端模块说明 (gira-backend/)

### gira-common
- 公共工具类
- 通用配置
- 常量定义
- 基础组件

### gira-core
- 核心业务逻辑
- 领域模型
- 业务服务
- 数据访问

### gira-auth
- 用户认证
- 权限管理
- 会话管理
- 安全配置

### gira-api
- REST API接口
- 接口文档
- 请求处理
- 响应封装

### gira-admin
- 管理后台功能
- 系统配置
- 运维管理

## 前端目录说明 (gira-frontend/src/)

### components
- 通用UI组件
- 业务组件
- 表单组件
- 列表组件

### features
- 认证模块
- Backlog模块
- 看板模块
- 项目管理
- 用户管理
- 系统设置

### services
- API调用封装
- 数据处理
- 缓存处理
- 工具函数

## 开发指南

### 环境要求
- JDK 17
- Node.js 18.19.0
- PostgreSQL 14.10
- Redis 7.2.3
- Docker 24.0.7

### 本地开发
1. 克隆项目
```bash
git clone https://github.com/your-org/gira.git
```

2. 启动后端
```bash
cd gira-backend
./mvnw spring-boot:run
```

3. 启动前端
```bash
cd gira-frontend
npm install
npm start
```

### 使用Docker
```bash
docker-compose up -d
```

## 贡献指南
1. Fork 项目
2. 创建特性分支
3. 提交代码
4. 创建Pull Request

## 许可证
[MIT License](LICENSE)
