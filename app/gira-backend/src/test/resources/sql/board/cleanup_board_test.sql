-- 清理测试数据
-- 1. 先删除任务（如果有）
DELETE FROM tasks WHERE column_id IN (
    SELECT id FROM board_columns WHERE board_id IN (
        SELECT id FROM boards WHERE name IN ('New Test Board', 'Test Board')
    )
);

-- 2. 删除看板列
DELETE FROM board_columns WHERE board_id IN (
    SELECT id FROM boards WHERE name IN ('New Test Board', 'Test Board')
);

-- 3. 删除看板
DELETE FROM boards WHERE name IN ('New Test Board', 'Test Board');

-- 4. 最后删除用户
DELETE FROM users WHERE username IN ('manager', 'developer'); 