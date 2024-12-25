# GIRA 数据库设计文档

## 1. 数据库环境

### 1.1 基本信息
- 数据库类型：PostgreSQL
- 版本：14.10
- 字符集：UTF-8
- 排序规则：utf8_general_ci

### 1.2 连接配置
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/gira
    username: gira_user
    password: ${GIRA_DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
      max-lifetime: 1200000
```

## 2. 表结构设计

### 2.1 用户认证模块

#### 2.1.1 用户表 (users)
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100),
    avatar_url VARCHAR(255),
    status SMALLINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
```

#### 2.1.2 角色表 (roles)
```sql
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 初始数据
INSERT INTO roles (name, description, is_system) VALUES
('ROLE_ADMIN', '系统管理员', true),
('ROLE_USER', '普通用户', true),
('ROLE_MANAGER', '项目经理', true);
```

#### 2.1.3 用户角色关联表 (user_roles)
```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- 索引
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
```

#### 2.1.4 权限表 (permissions)
```sql
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

#### 2.1.5 角色权限关联表 (role_permissions)
```sql
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- 索引
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);
```

### 2.2 团队管理模块

#### 2.2.1 团队表 (teams)
```sql
CREATE TABLE teams (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_by BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX idx_teams_created_by ON teams(created_by);
```

#### 2.2.2 团队成员表 (team_members)
```sql
CREATE TABLE team_members (
    id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX idx_team_members_team_id ON team_members(team_id);
CREATE INDEX idx_team_members_user_id ON team_members(user_id);
```

### 2.3 项目管理模块

#### 2.3.1 项目表 (projects)
```sql
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    key VARCHAR(10) NOT NULL UNIQUE,
    description TEXT,
    status SMALLINT NOT NULL DEFAULT 1,
    owner_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 索引
CREATE INDEX idx_projects_owner ON projects(owner_id);
CREATE INDEX idx_projects_key ON projects(key);
CREATE INDEX idx_projects_status ON projects(status);
```

#### 2.3.2 项目成员表 (project_members)
```sql
CREATE TABLE project_members (
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (project_id, user_id)
);

-- 索引
CREATE INDEX idx_project_members_project ON project_members(project_id);
CREATE INDEX idx_project_members_user ON project_members(user_id);
```

### 2.4 看板管理模块

#### 2.4.1 看板表 (boards)
```sql
CREATE TABLE boards (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    archived BOOLEAN NOT NULL DEFAULT FALSE,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- 索引
CREATE INDEX idx_boards_project ON boards(project_id);
```

#### 2.4.2 看板列表 (board_columns)
```sql
CREATE TABLE board_columns (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    position INTEGER,
    board_id BIGINT NOT NULL REFERENCES boards(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- 索引
CREATE INDEX idx_board_columns_board ON board_columns(board_id);
```

### 2.5 任务管理模块

#### 2.5.1 任务表 (tasks)
```sql
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL,
    column_id BIGINT NOT NULL REFERENCES board_columns(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- 索引
CREATE INDEX idx_tasks_column ON tasks(column_id);
```

#### 2.5.2 问题表 (issues)
```sql
CREATE TABLE issues (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority INTEGER NOT NULL,
    assignee_id BIGINT REFERENCES users(id),
    due_date TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    story_points INTEGER,
    project_id BIGINT NOT NULL REFERENCES projects(id),
    sprint_id BIGINT REFERENCES sprints(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX idx_issues_assignee ON issues(assignee_id);
CREATE INDEX idx_issues_project ON issues(project_id);
CREATE INDEX idx_issues_sprint ON issues(sprint_id);
```

#### 2.5.3 问题标签表 (issue_labels)
```sql
CREATE TABLE issue_labels (
    issue_id BIGINT NOT NULL REFERENCES issues(id) ON DELETE CASCADE,
    label VARCHAR(100) NOT NULL
);

-- 索引
CREATE INDEX idx_issue_labels_issue ON issue_labels(issue_id);
```

#### 2.5.4 问题附件关联表 (issue_attachments)
```sql
CREATE TABLE issue_attachments (
    issue_id BIGINT NOT NULL REFERENCES issues(id) ON DELETE CASCADE,
    attachment VARCHAR(255) NOT NULL
);

-- 索引
CREATE INDEX idx_issue_attachments_issue ON issue_attachments(issue_id);
```

### 2.6 评论模块

#### 2.6.1 评论表 (comments)
```sql
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    author_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX idx_comments_task ON comments(task_id);
CREATE INDEX idx_comments_author ON comments(author_id);
```

### 2.7 附件模块

#### 2.7.1 附件表 (attachments)
```sql
CREATE TABLE attachments (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    size BIGINT,
    path VARCHAR(255) NOT NULL,
    task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- 索引
CREATE INDEX idx_attachments_task ON attachments(task_id);
CREATE INDEX idx_attachments_user ON attachments(user_id);
```

### 2.8 标签模块

#### 2.8.1 标签表 (labels)
```sql
CREATE TABLE labels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(50),
    project_id BIGINT REFERENCES projects(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX idx_labels_project ON labels(project_id);
```

## 3. 数据库优化

### 3.1 索引策略
- 对所有外键字段创建索引
- 对常用查询字段创建索引
- 对需要排序的字段创建索引
- 对唯一约束字段创建唯一索引

### 3.2 审计字段
所有主要表都包含以下审计字段：
- created_at：创建时间
- updated_at：更新时间
- created_by：创建者（部分表）
- updated_by：更新者（部分表）

### 3.3 软删除
某些重要表（如projects）使用deleted_at字段实现软删除功能。

### 3.4 数据类型选择
- 使用BIGSERIAL作为ID类型，支持大量数据
- 使用TEXT类型存储长文本内容
- 使用TIMESTAMP类型存储时间信息
- 使用VARCHAR类型存储有长度限制的字符串

### 3.5 约束设计
- 使用外键约束确保数据完整性
- 使用NOT NULL约束确保必要字段有值
- 使用UNIQUE约束确保字段唯一性
- 使用DEFAULT值设置默认数据
``` 