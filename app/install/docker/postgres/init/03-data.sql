-- 插入基础角色
INSERT INTO roles (name, description) VALUES
('ROLE_ADMIN', '系统管理员'),
('ROLE_USER', '普通用户')
ON CONFLICT (name) DO NOTHING;

-- 插入基础权限
INSERT INTO permissions (name, description) VALUES
('USER_CREATE', '创建用户'),
('USER_READ', '读取用户信息'),
('USER_UPDATE', '更新用户信息'),
('USER_DELETE', '删除用户'),
('ROLE_MANAGE', '角色管理'),
('PERMISSION_MANAGE', '权限管理')
ON CONFLICT (name) DO NOTHING;

-- 为管理员角色分配所有权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_ADMIN';

-- 为普通用户角色分配基本权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_USER'
AND p.name IN ('USER_READ');

-- 插入管理员用户 (密码: 1qaz@WSX)
INSERT INTO users (username, password, email, full_name, status)
VALUES (
    'admin',
    '$2a$10$KXQpxKHQe8YuCGj3dJxsZOJEcA5Y6xvQr1yMAcWqsX8TXsYA8h0eO',  -- 1qaz@WSX
    'admin@gira.com',
    'System Administrator',
    1
)
ON CONFLICT (username) DO NOTHING;

-- 为管理员用户分配管理员角色
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin'
AND r.name = 'ROLE_ADMIN'; 