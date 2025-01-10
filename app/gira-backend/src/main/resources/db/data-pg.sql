-- PostgreSQL数据库初始化脚本
-- 插入管理员用户
-- 密码是: admin123
INSERT INTO users (username, password, email, full_name, role, status, created_at, updated_at)
VALUES ('admin', '$2a$10$rAYxqUqP.1nqZgYFZiPOxuS8OMxhvHzH.Y2.Qh0qKqvq4YuNJa5Uy', 'admin@example.com', '系统管理员', 'ADMIN', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入默认看板
WITH inserted_board AS (
    INSERT INTO boards (name, description, status, created_by, created_at, updated_at)
    SELECT '默认看板', '系统默认看板', 'ACTIVE', id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    FROM users WHERE username = 'admin'
    RETURNING id
)
-- 插入默认看板列
INSERT INTO board_columns (board_id, name, wip_limit, order_index, created_at, updated_at)
SELECT 
    id, unnest(ARRAY['待办', '进行中', '已完成']),
    unnest(ARRAY[NULL, 3, NULL]),
    unnest(ARRAY[0, 1, 2]),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM inserted_board; 