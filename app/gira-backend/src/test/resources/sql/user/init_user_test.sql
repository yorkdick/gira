-- 插入测试用户数据
INSERT INTO users (username, password, email, full_name, role, status, created_at, updated_at)
VALUES ('testuser', '$2a$10$kWVUP88bSsV3ouhxLj1FlOVmFnHEZsAxBqjRgXISe1o4qtTzHSa1S', 'test@example.com', 'Test User', 'DEVELOPER', 'ACTIVE', NOW(), NOW()); 