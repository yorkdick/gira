# 数据目录说明

## 目录结构

```
data/
├── postgres/           # PostgreSQL数据文件
└── redis/             # Redis数据文件

logs/                  # 应用日志目录
uploads/              # 文件上传目录
```

## 数据持久化

### PostgreSQL数据
- 位置：`data/postgres/`
- 用途：存储所有业务数据，包括用户、项目、任务等
- 特点：完整的数据持久化，支持备份和恢复
- 注意：该目录已添加到.gitignore，不会被提交到版本控制系统

### Redis数据
- 位置：`data/redis/`
- 用途：存储缓存数据，包括会话信息、令牌等
- 特点：支持持久化，但主要用于提升系统性能
- 注意：该目录已添加到.gitignore，不会被提交到版本控制系统

### 日志文件
- 位置：`logs/`
- 用途：存储应用运行日志
- 内容：包括系统日志、错误日志、审计日志等
- 建议：定期清理或归档旧日志

### 上传文件
- 位置：`uploads/`
- 用途：存储用户上传的文件
- 支持：图片、文档等文件类型
- 建议：定期备份重要文件

## 初始化说明

### 数据库初始化
1. 首次启动时会自动创建必要的数据表
2. 默认创建管理员账户：
   - 用户名：admin
   - 密码：1qaz@WSX

### 目录权限
1. Linux/MacOS环境需要设置适当的权限：
   ```bash
   chmod -R 777 data logs uploads
   ```

2. Windows环境通常不需要特别设置权限

## 数据备份

### 数据库备份
```bash
# 创建备份
docker-compose exec postgres pg_dump -U postgres gira > backup_$(date +%Y%m%d).sql

# 恢复备份
docker-compose exec -T postgres psql -U postgres gira < backup_file.sql
```

### 文件备份
```bash
# 备份数据目录
tar -czf data_backup_$(date +%Y%m%d).tar.gz data/

# 备份上传文件
tar -czf uploads_backup_$(date +%Y%m%d).tar.gz uploads/
```

## 注意事项

1. 数据安全
   - 定期备份重要数据
   - 妥善保管备份文件
   - 定期检查磁盘空间

2. 性能优化
   - 定期清理不必要的日志文件
   - 及时清理临时文件
   - 监控数据目录大小

3. 版本控制
   - 数据目录已添加到.gitignore
   - 不要提交敏感数据到代码仓库
   - 保持备份文件的安全性

4. 运维建议
   - 监控磁盘使用情况
   - 设置日志轮转策略
   - 制定备份恢复预案
``` 