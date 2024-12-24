# GIRA 部署指南

本文档提供了GIRA项目的详细部署步骤，包括开发环境、测试环境和生产环境的部署说明。

## 1. 部署架构

### 1.1 系统架构图
```
[用户] --> [负载均衡器(Nginx)]
                 |
    -------------------------
    |           |           |
[前端服务1]  [前端服务2]  [前端服务3]
    |           |           |
[后端服务1]  [后端服务2]  [后端服务3]
    |           |           |
    -------------------------
            |
    [数据库主从集群]
    [Redis集群]
```

### 1.2 部署方案
- 开发环境：单机部署
- 测试环境：单机Docker容器化部署
- 生产环境：多机集群部署

## 2. 开发环境部署

### 2.1 前端部署
```bash
cd app/gira-frontend

# 安装依赖
npm install

# 启动开发服务器
npm start
```

### 2.2 后端部署
```bash
cd app/gira-backend

# 编译打包
./mvnw clean package -DskipTests

# 运行应用
java -jar target/gira-backend.jar
```

## 3. 测试环境部署

### 3.1 准备Docker环境
```bash
# 安装Docker和Docker Compose
sudo apt update
sudo apt install docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker
```

### 3.2 配置Docker Compose
创建 `docker-compose.yml`:
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:14.10
    environment:
      POSTGRES_DB: gira
      POSTGRES_USER: gira_user
      POSTGRES_PASSWORD: your_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  redis:
    image: redis:7.2.3
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  backend:
    build: ./app/gira-backend
    depends_on:
      - postgres
      - redis
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/gira
      SPRING_DATASOURCE_USERNAME: gira_user
      SPRING_DATASOURCE_PASSWORD: your_password
      SPRING_REDIS_HOST: redis
    ports:
      - "8080:8080"

  frontend:
    build: ./app/gira-frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  postgres_data:
  redis_data:
```

### 3.3 启动服务
```bash
docker-compose up -d
```

## 4. 生产环境部署

### 4.1 服务器准备
- 负载均衡服务器：2台
- 应用服务器：6台（3台前端，3台后端）
- 数据库服务器：3台（1主2从）
- Redis服务器：3台（集群模式）

### 4.2 负载均衡配置
安装并配置Nginx：
```bash
sudo apt install nginx

# 编辑nginx.conf
sudo vim /etc/nginx/nginx.conf
```

Nginx配置示例：
```nginx
http {
    upstream frontend {
        server frontend1:80;
        server frontend2:80;
        server frontend3:80;
    }

    upstream backend {
        server backend1:8080;
        server backend2:8080;
        server backend3:8080;
    }

    server {
        listen 80;
        server_name example.com;

        location / {
            proxy_pass http://frontend;
        }

        location /api {
            proxy_pass http://backend;
        }

        location /ws {
            proxy_pass http://backend;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
        }
    }
}
```

### 4.3 数据库集群配置

#### 主库配置
编辑 `postgresql.conf`:
```
wal_level = replica
max_wal_senders = 10
max_replication_slots = 10
```

#### 从库配置
```bash
# 创建复制用户
CREATE USER replicator WITH REPLICATION ENCRYPTED PASSWORD 'your_password';

# 配置从库
pg_basebackup -h master_host -D data_dir -U replicator -v -P
```

### 4.4 Redis集群配置
```bash
# 创建Redis集群
redis-cli --cluster create \
    redis1:6379 redis2:6379 redis3:6379 \
    --cluster-replicas 1
```

### 4.5 应用部署

#### 后端服务
```bash
# 创建系统服务
sudo vim /etc/systemd/system/gira-backend.service

[Unit]
Description=GIRA Backend Service
After=network.target

[Service]
User=gira
ExecStart=/usr/bin/java -jar /opt/gira/gira-backend.jar
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target

# 启动服务
sudo systemctl enable gira-backend
sudo systemctl start gira-backend
```

#### 前端服务
```bash
# 构建前端
npm run build

# 配置Nginx
server {
    listen 80;
    root /var/www/gira;
    index index.html;
    try_files $uri $uri/ /index.html;
}
```

## 5. 监控和日志

### 5.1 监控配置
```yaml
# Prometheus配置
scrape_configs:
  - job_name: 'gira'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['backend1:8080', 'backend2:8080', 'backend3:8080']
```

### 5.2 日志收集
```yaml
# Filebeat配置
filebeat.inputs:
- type: log
  paths:
    - /opt/gira/logs/*.log
output.elasticsearch:
  hosts: ["elasticsearch:9200"]
```

## 6. 备份策略

### 6.1 数据库备份
```bash
# 创建备份脚本
#!/bin/bash
BACKUP_DIR="/backup/postgres"
DATE=$(date +%Y%m%d)
pg_dump -U gira_user -F c -b -v -f "$BACKUP_DIR/gira_$DATE.dump" gira
```

### 6.2 应用配置备份
```bash
# 备份配置文件
tar -czf /backup/config/gira_config_$(date +%Y%m%d).tar.gz /opt/gira/config/
```

## 7. 回滚策略

### 7.1 应用回滚
```bash
# 回滚到指定版本
cd /opt/gira
mv current current_$(date +%Y%m%d)
ln -s versions/gira-1.0.0 current
systemctl restart gira-backend
```

### 7.2 数据库回滚
```bash
# 恢复数据库
pg_restore -U gira_user -d gira /backup/postgres/gira_20231220.dump
```

## 8. 性能优化

### 8.1 JVM优化
```bash
JAVA_OPTS="-Xms4g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### 8.2 Nginx优化
```nginx
worker_processes auto;
worker_connections 1024;
keepalive_timeout 65;
gzip on;
```

## 9. 安全加固

### 9.1 系统加固
```bash
# 禁用root SSH登录
sed -i 's/PermitRootLogin yes/PermitRootLogin no/' /etc/ssh/sshd_config

# 配置防火墙
ufw allow 80/tcp
ufw allow 443/tcp
ufw enable
```

### 9.2 应用安全
```yaml
# Spring Security配置
security:
  require-ssl: true
  basic:
    enabled: false
  jwt:
    secret: your_secret_key
    expiration: 86400
```

## 10. 故障处理

### 10.1 常见问题处理
1. 服务无法启动
   - 检查日志文件
   - 验证配置文件
   - 确认端口占用

2. 数据库连接失败
   - 检查网络连接
   - 验证用户权限
   - 检查连接池配置

3. 性能问题
   - 检查系统资源使��
   - 分析慢查询日志
   - 优化JVM参数

### 10.2 紧急联系人
- 系统管理员：admin@example.com
- 数据库管理员：dba@example.com
- 运维团队：ops@example.com 