-- H2数据库初始化脚本
-- 插入管理员用户
-- 密码是: admin123
INSERT INTO users (username, password, email, full_name, role, status, created_at, updated_at)
VALUES ('admin', '$2a$10$31kC8I8pQmMmhHi4FvfkJOUxW9jlJEZpNoGcevDXacVKszPG4tDMy', 'admin@example.com', '系统管理员', 'ADMIN', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入默认看板
INSERT INTO boards (name, description, status, created_by, created_at, updated_at)
SELECT '默认看板', '系统默认看板', 'ACTIVE', id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users WHERE username = 'admin';

-- 插入默认看板列
WITH default_board AS (
    SELECT id FROM boards WHERE name = '默认看板'
)
INSERT INTO board_columns (board_id, name, order_index, created_at, updated_at)
SELECT id, '待办', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM default_board
UNION ALL
SELECT id, '进行中', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM default_board
UNION ALL
SELECT id, '已完成', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM default_board; 