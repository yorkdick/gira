-- 清理测试数据
-- 1. 删除测试任务（只删除测试过程中创建的任务）
DELETE FROM tasks 
WHERE board_id IN (SELECT id FROM boards WHERE name = '测试看板')
   OR reporter_id IN (SELECT id FROM users WHERE username IN ('manager', 'developer'))
   OR assignee_id IN (SELECT id FROM users WHERE username IN ('manager', 'developer'));

-- 2. 删除测试看板列
DELETE FROM board_columns WHERE board_id IN (
    SELECT id FROM boards WHERE name = '测试看板'
);

-- 3. 删除测试看板
DELETE FROM boards WHERE name = '测试看板';

-- 4. 删除测试用户
DELETE FROM users WHERE username IN ('manager', 'developer'); 