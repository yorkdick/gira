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

-- 初始数据
INSERT INTO permissions (name, description) VALUES
('CREATE_PROJECT', '创建项目'),
('MANAGE_USERS', '管理用户'),
('MANAGE_ROLES', '管理角色');
```

### 2.2 项目管理模块

#### 2.2.1 项目表 (projects)
```sql
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    key VARCHAR(10) NOT NULL UNIQUE,
    description TEXT,
    owner_id BIGINT NOT NULL REFERENCES users(id),
    status SMALLINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 索引
CREATE INDEX idx_projects_owner ON projects(owner_id);
CREATE INDEX idx_projects_key ON projects(key);
CREATE INDEX idx_projects_status ON projects(status);
```

#### 2.2.2 项目成员表 (project_members)
```sql
CREATE TABLE project_members (
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL, -- OWNER, ADMIN, MEMBER
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (project_id, user_id)
);

-- 索引
CREATE INDEX idx_project_members_project ON project_members(project_id);
CREATE INDEX idx_project_members_user ON project_members(user_id);
```

### 2.3 任务管理模块

#### 2.3.1 问题类型表 (issue_types)
```sql
CREATE TABLE issue_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    icon VARCHAR(255),
    description VARCHAR(255),
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 初始数据
INSERT INTO issue_types (name, description, project_id) VALUES
('Story', '用户故事', 1),
('Task', '任务', 1),
('Bug', '缺陷', 1);
```

#### 2.3.2 问题状态表 (issue_status)
```sql
CREATE TABLE issue_status (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    category VARCHAR(20) NOT NULL, -- TODO, IN_PROGRESS, DONE
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    order_index INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX idx_issue_status_project ON issue_status(project_id);
CREATE INDEX idx_issue_status_category ON issue_status(category);
```

#### 2.3.3 问题表 (issues)
```sql
CREATE TABLE issues (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type_id BIGINT NOT NULL REFERENCES issue_types(id),
    status_id BIGINT NOT NULL REFERENCES issue_status(id),
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    reporter_id BIGINT NOT NULL REFERENCES users(id),
    assignee_id BIGINT REFERENCES users(id),
    priority SMALLINT NOT NULL DEFAULT 3,
    due_date TIMESTAMP,
    estimated_hours DECIMAL(10,2),
    spent_hours DECIMAL(10,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 索引
CREATE INDEX idx_issues_project ON issues(project_id);
CREATE INDEX idx_issues_type ON issues(type_id);
CREATE INDEX idx_issues_status ON issues(status_id);
CREATE INDEX idx_issues_assignee ON issues(assignee_id);
CREATE INDEX idx_issues_reporter ON issues(reporter_id);
CREATE INDEX idx_issues_priority ON issues(priority);
CREATE INDEX idx_issues_created ON issues(created_at);
```

#### 2.3.4 问题评论表 (issue_comments)
```sql
CREATE TABLE issue_comments (
    id BIGSERIAL PRIMARY KEY,
    issue_id BIGINT NOT NULL REFERENCES issues(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 索引
CREATE INDEX idx_comments_issue ON issue_comments(issue_id);
CREATE INDEX idx_comments_user ON issue_comments(user_id);
```

### 2.4 附件管理模块

#### 2.4.1 附件表 (attachments)
```sql
CREATE TABLE attachments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    path VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    issue_id BIGINT NOT NULL REFERENCES issues(id) ON DELETE CASCADE,
    uploader_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 索引
CREATE INDEX idx_attachments_issue ON attachments(issue_id);
CREATE INDEX idx_attachments_uploader ON attachments(uploader_id);
```

## 3. 数据库优化

### 3.1 索引策略
- 对常用查询字段创建索引
- 对外键字段创建索引
- 对排序字段创建索引
- 使用复合索引优化多字段查询

### 3.2 分区策略
```sql
-- 按时间分区的示例（issues表）
CREATE TABLE issues_partition OF issues
PARTITION BY RANGE (created_at);

-- 创建月度分区
CREATE TABLE issues_y2024m01 
PARTITION OF issues_partition
FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

CREATE TABLE issues_y2024m02 
PARTITION OF issues_partition
FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');
```

### 3.3 视图定义
```sql
-- 项目统计视图
CREATE VIEW project_statistics AS
SELECT 
    p.id AS project_id,
    p.name AS project_name,
    COUNT(DISTINCT i.id) AS total_issues,
    COUNT(DISTINCT CASE WHEN i.status_id IN (SELECT id FROM issue_status WHERE category = 'DONE') THEN i.id END) AS completed_issues,
    COUNT(DISTINCT pm.user_id) AS member_count
FROM projects p
LEFT JOIN issues i ON p.id = i.project_id
LEFT JOIN project_members pm ON p.id = pm.project_id
GROUP BY p.id, p.name;

-- 用户工作量视图
CREATE VIEW user_workload AS
SELECT 
    u.id AS user_id,
    u.username,
    COUNT(i.id) AS assigned_issues,
    SUM(i.estimated_hours) AS total_estimated_hours,
    SUM(i.spent_hours) AS total_spent_hours
FROM users u
LEFT JOIN issues i ON u.id = i.assignee_id
WHERE i.deleted_at IS NULL
GROUP BY u.id, u.username;
```

## 4. 数据迁移

### 4.1 Flyway配置
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    baseline-version: 0
    validate-on-migrate: true
```

### 4.2 迁移脚本示例
```sql
-- V1__init_schema.sql
CREATE TABLE users (
    -- 表结构定义
);

-- V2__add_indexes.sql
CREATE INDEX idx_users_email ON users(email);

-- V3__add_foreign_keys.sql
ALTER TABLE issues
ADD CONSTRAINT fk_issues_project
FOREIGN KEY (project_id) REFERENCES projects(id);
```

## 5. 数据备份策略

### 5.1 备份脚本
```bash
#!/bin/bash
BACKUP_DIR="/backup/postgres"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
DB_NAME="gira"

# 创建备份
pg_dump -Fc -U postgres $DB_NAME > $BACKUP_DIR/$DB_NAME_$TIMESTAMP.dump

# 保留最近30天的备份
find $BACKUP_DIR -type f -mtime +30 -delete
```

### 5.2 恢复脚本
```bash
#!/bin/bash
BACKUP_FILE=$1
DB_NAME="gira"

# 恢复数据库
pg_restore -U postgres -d $DB_NAME $BACKUP_FILE
```

## 6. 监控和维护

### 6.1 性能监控视图
```sql
-- 慢查询监控
CREATE VIEW slow_queries AS
SELECT 
    query,
    calls,
    total_time,
    mean_time,
    rows
FROM pg_stat_statements
ORDER BY total_time DESC;

-- 表空间使用情况
CREATE VIEW table_sizes AS
SELECT
    relname as table_name,
    pg_size_pretty(pg_total_relation_size(relid)) as total_size,
    pg_size_pretty(pg_relation_size(relid)) as table_size,
    pg_size_pretty(pg_total_relation_size(relid) - pg_relation_size(relid)) as index_size
FROM pg_catalog.pg_statio_user_tables
ORDER BY pg_total_relation_size(relid) DESC;
```

### 6.2 维护任务
```sql
-- 定期VACUUM
VACUUM ANALYZE;

-- 更新统计信息
ANALYZE;

-- 重建索引
REINDEX TABLE issues;
``` 