-- 插入测试用户
INSERT INTO users (username, password, email, full_name, role, status, created_at, updated_at)
VALUES 
('manager', '$2a$10$rAYxqUqP.1nqZgYFZiPOxuS8OMxhvHzH.Y2.Qh0qKqvq4YuNJa5Uy', 'manager@example.com', '项目经理', 'ADMIN', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('developer', '$2a$10$rAYxqUqP.1nqZgYFZiPOxuS8OMxhvHzH.Y2.Qh0qKqvq4YuNJa5Uy', 'developer@example.com', '开发人员', 'DEVELOPER', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入测试看板
INSERT INTO boards (name, description, status, created_by, created_at, updated_at)
SELECT '测试看板', '用于Task测试的看板', 'ACTIVE', id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users WHERE username = 'manager';

-- 插入看板列
INSERT INTO board_columns (name, board_id, order_index, created_at, updated_at)
SELECT '待办', id, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM boards WHERE name = '测试看板'
UNION ALL
SELECT '进行中', id, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM boards WHERE name = '测试看板'
UNION ALL
SELECT '已完成', id, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM boards WHERE name = '测试看板'; 