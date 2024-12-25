# GIRA 安装说明

## 环境要求

### 开发环境
- JDK 17
- Node.js 18.19.0
- Maven 3.9+
- Docker 24.0.7
- Docker Compose 2.21.0+
- Git

### 运行环境
- PostgreSQL 14.10
- Redis 7.2.3
- 操作系统：Linux/Windows/MacOS

## 安装步骤

### 1. 克隆项目
```bash
git clone https://github.com/your-org/gira.git
cd gira
```

### 2. 环境准备

#### 2.1 创建必要的目录
```bash
# 创建数据目录
mkdir -p data/postgres data/redis

# 创建日志和上传文件目录
mkdir -p logs uploads
```

#### 2.2 设置目录权限（Linux/MacOS）
```bash
# 设置数据目录权限
chmod -R 777 data
chmod -R 777 logs
chmod -R 777 uploads
```

### 3. 使用Docker Compose部署

#### 3.1 开发环境
```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f
```

#### 3.2 生产环境
1. 修改环境变量
   ```bash
   # 复制示例配置
   cp .env.example .env
   
   # 编辑配置文件，设置生产环境的密码和密钥
   vim .env
   ```

2. 启动服务
   ```bash
   docker-compose -f docker-compose.prod.yml up -d
   ```

### 4. 验证安装

1. 检查服务状态：
   ```bash
   docker-compose ps
   ```
   确保所有服务都��� `healthy` 状态

2. 访问API文档：
   - 地址：http://localhost:8080/api/swagger-ui.html
   - 默认管理员账户：
     - 用户名：admin
     - 密码：1qaz@WSX

3. 检查数据库：
   ```bash
   # 连接到PostgreSQL
   docker-compose exec postgres psql -U postgres -d gira

   # 检查表是否创建
   \dt
   ```

## 常见问题

### 1. 端口冲突
如果遇到端口冲突，可以修改 `docker-compose.yml` 中的端口映射：
```yaml
ports:
  - "自定义端口:原始端口"
```

### 2. 数据库连接失败
1. 检查PostgreSQL服务是否启动：
   ```bash
   docker-compose ps postgres
   ```

2. 检查数据库日志：
   ```bash
   docker-compose logs postgres
   ```

### 3. Redis连接问题
1. 检查Redis服务状态：
   ```bash
   docker-compose exec redis redis-cli ping
   ```

2. 检查Redis日志：
   ```bash
   docker-compose logs redis
   ```

## 备份和恢复

### 数据库备份
```bash
# 创建备份
docker-compose exec postgres pg_dump -U postgres gira > backup_$(date +%Y%m%d).sql

# 恢复备份
docker-compose exec -T postgres psql -U postgres gira < backup_file.sql
```

### 数据目录备份
```bash
# 备份
tar -czf data_backup_$(date +%Y%m%d).tar.gz data/

# 恢复
tar -xzf data_backup_[日期].tar.gz
```

## 更新说明

### ��新步骤
1. 拉取最新代码：
   ```bash
   git pull origin main
   ```

2. 重新构建服务：
   ```bash
   docker-compose build
   ```

3. 更新服务：
   ```bash
   docker-compose up -d
   ```

### 数据迁移
- 数据库结构更新会在容器启动时自动执行
- 请在更新前备份数据

## 安全建议

1. 生产环境配置：
   - 修改默认密码
   - 使用强密钥
   - 限制端口访问
   - 启用SSL/TLS

2. 定期维护：
   - 更新依赖包
   - 备份数据
   - 检查日志
   - 监控系统资源

## 支持和帮助

如果遇到问题：
1. 检查日志文件
2. 查看 [项目Wiki](wiki-url)
3. 提交 [Issue](issues-url)
4. 联系技术支持 