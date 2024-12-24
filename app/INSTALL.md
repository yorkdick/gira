# GIRA 安装指南

本文档提供了GIRA项目的详细安装步骤。

## 1. 环境要求

### 1.1 基础环境
- JDK 17.0.9-LTS
- Node.js 18.19.0 LTS
- npm 10.2.3
- PostgreSQL 14.10
- Redis 7.2.3
- Docker 24.0.7（可选，如使用容器化部署）
- Docker Compose 2.23.3（可选，如使用容器化部署）

### 1.2 推荐IDE
- 后端: IntelliJ IDEA 2023.3
- 前端: Visual Studio Code 1.85.1
- 数据库: DBeaver 23.3.0

## 2. 数据库安装

### 2.1 PostgreSQL安装
```bash
# Windows使用官方安装包
# 下载地址：https://www.postgresql.org/download/windows/

# Linux (Ubuntu)
sudo apt update
sudo apt install postgresql-14
sudo systemctl start postgresql
sudo systemctl enable postgresql

# 创建数据库和用户
sudo -u postgres psql
CREATE DATABASE gira;
CREATE USER gira_user WITH ENCRYPTED PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE gira TO gira_user;
```

### 2.2 Redis安装
```bash
# Windows使用官方安装包或WSL
# 下载地址：https://github.com/microsoftarchive/redis/releases

# Linux (Ubuntu)
sudo apt update
sudo apt install redis-server
sudo systemctl start redis
sudo systemctl enable redis
```

## 3. 后端安装

### 3.1 克隆代码
```bash
git clone https://github.com/your-org/gira.git
cd gira/app/gira-backend
```

### 3.2 配置数据库连接
编辑 `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/gira
    username: gira_user
    password: your_password
  redis:
    host: localhost
    port: 6379
```

### 3.3 编译打包
```bash
# 使用Maven打包
./mvnw clean package -DskipTests

# 或使用Maven Wrapper
./mvnw clean package -DskipTests
```

### 3.4 运行应用
```bash
java -jar target/gira-backend.jar
```

## 4. 前端安装

### 4.1 安装依赖
```bash
cd gira/app/gira-frontend
npm install
```

### 4.2 配置环境变量
创建 `.env.local` 文件：
```
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_WS_URL=ws://localhost:8080/ws
```

### 4.3 构建和运行
```bash
# 开发环境
npm start

# 生产环境构建
npm run build
```

## 5. Docker部署

### 5.1 使用Docker Compose
```bash
cd gira
docker-compose up -d
```

### 5.2 单独容器部署

#### 后端
```bash
cd app/gira-backend
docker build -t gira-backend .
docker run -d -p 8080:8080 gira-backend
```

#### 前端
```bash
cd app/gira-frontend
docker build -t gira-frontend .
docker run -d -p 80:80 gira-frontend
```

## 6. 验证安装

### 6.1 检查服务状态
```bash
# 检查后端服务
curl http://localhost:8080/actuator/health

# 检查前端服务
curl http://localhost:80
```

### 6.2 访问应用
- 前端访问地址：http://localhost:80
- API文档地址：http://localhost:8080/swagger-ui.html
- 管理后台：http://localhost:80/admin

## 7. 常见问题

### 7.1 数据库连接问题
1. 检查PostgreSQL服务是否运行
2. 验证数据库用户权限
3. 确认防火墙设置

### 7.2 Redis连接问题
1. 检查Redis服务状态
2. 验证Redis密码配置
3. 检查端口是否开放

### 7.3 前端构建问题
1. 清理node_modules并重新安装
2. 检查Node.js版本兼容性
3. 更新npm缓存

## 8. 安全配置

### 8.1 生产环境配置
1. 修改默认端口
2. 配置HTTPS
3. 设置密码策略
4. 配置跨域规则

### 8.2 防火墙配置
```bash
# 开放必要端口
sudo ufw allow 80/tcp
sudo ufw allow 8080/tcp
sudo ufw allow 5432/tcp
sudo ufw allow 6379/tcp
```

## 9. 备份和恢复

### 9.1 数据库备份
```bash
# 备份
pg_dump -U gira_user -F c -b -v -f backup.dump gira

# 恢复
pg_restore -U gira_user -d gira backup.dump
```

### 9.2 应用配置备份
```bash
# 备份配置文件
cp src/main/resources/application.yml application.yml.bak
```

## 10. 监控配置

### 10.1 Actuator配置
编辑 `application.yml`:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
```

### 10.2 日志配置
编辑 `logback-spring.xml`:
```xml
<configuration>
    <property name="LOG_PATH" value="./logs"/>
    <property name="LOG_FILE" value="${LOG_PATH}/gira.log"/>
    <!-- 日志配置详情 -->
</configuration>
``` 