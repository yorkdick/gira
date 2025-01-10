-- 清理测试数据
DELETE FROM tasks WHERE sprint_id IN (SELECT id FROM sprints WHERE board_id IN (SELECT id FROM boards WHERE name = '测试看板'));
DELETE FROM sprints WHERE board_id IN (SELECT id FROM boards WHERE name = '测试看板');
DELETE FROM boards WHERE name = '测试看板';
DELETE FROM users WHERE username IN ('manager', 'developer'); 