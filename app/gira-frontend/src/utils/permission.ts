import { User } from '@/types/user';

// 权限常量
export const Permissions = {
  // 项目权限
  PROJECT_VIEW: 'project:view',
  PROJECT_CREATE: 'project:create',
  PROJECT_EDIT: 'project:edit',
  PROJECT_DELETE: 'project:delete',

  // 任务权限
  TASK_VIEW: 'task:view',
  TASK_CREATE: 'task:create',
  TASK_EDIT: 'task:edit',
  TASK_DELETE: 'task:delete',
  TASK_ASSIGN: 'task:assign',

  // Sprint权限
  SPRINT_VIEW: 'sprint:view',
  SPRINT_CREATE: 'sprint:create',
  SPRINT_EDIT: 'sprint:edit',
  SPRINT_DELETE: 'sprint:delete',

  // 用户权限
  USER_VIEW: 'user:view',
  USER_CREATE: 'user:create',
  USER_EDIT: 'user:edit',
  USER_DELETE: 'user:delete',
} as const;

// 角色权限映射
export const RolePermissions: Record<string, string[]> = {
  ADMIN: Object.values(Permissions),
  DEVELOPER: [
    Permissions.PROJECT_VIEW,
    Permissions.TASK_VIEW,
    Permissions.TASK_CREATE,
    Permissions.TASK_EDIT,
    Permissions.TASK_ASSIGN,
    Permissions.SPRINT_VIEW,
  ],
};

// 检查用户是否有特定权限
export const checkPermission = (user: User | null, permission: string): boolean => {
  if (!user) return false;
  
  // 管理员拥有所有权限
  if (user.role === 'ADMIN') return true;
  
  // 检查用户角色的权限
  const rolePermissions = RolePermissions[user.role];
  if (rolePermissions?.includes(permission)) return true;
  
  // 检查用户的额外权限
  return user.permissions?.includes(permission) || false;
};

// 检查用户是否有多个权限中的任意一个
export const checkAnyPermission = (user: User | null, permissions: string[]): boolean => {
  return permissions.some(permission => checkPermission(user, permission));
};

// 检查用户是否有所有指定的权限
export const checkAllPermissions = (user: User | null, permissions: string[]): boolean => {
  return permissions.every(permission => checkPermission(user, permission));
}; 