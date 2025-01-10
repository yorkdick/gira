-- 插入测试用户（看板管理员和普通用户）
INSERT INTO users (username, password, email, full_name, role, status, created_at, updated_at)
VALUES 
    ('manager', '$2a$10$rPiEAgQNIT1TCoKi3Eqq8eVaRYIRlR29Vw5f1fYaNwkh8/YkFEzZi', 'manager@example.com', 'Board Manager', 'ADMIN', 'ACTIVE', NOW(), NOW()),
    ('developer', '$2a$10$rPiEAgQNIT1TCoKi3Eqq8eVaRYIRlR29Vw5f1fYaNwkh8/YkFEzZi', 'dev@example.com', 'Board Developer', 'DEVELOPER', 'ACTIVE', NOW(), NOW()); 