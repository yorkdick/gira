// 用户角色
export enum UserRole {
  ADMIN = 'ADMIN',
  DEVELOPER = 'DEVELOPER'
}

// 登录请求参数
export interface LoginParams {
  username: string;
  password: string;
  remember?: boolean;
}

// 登录响应数据
export interface LoginResult {
  token: string;
  user: {
    id: number;
    username: string;
    email: string;
    role: UserRole;
  };
}

// 注册请求参数
export interface RegisterParams {
  username: string;
  password: string;
  email: string;
}

// 注册响应数据
export interface RegisterResult {
  id: number;
  username: string;
  email: string;
  role: UserRole;
}

// 修改密码请求参数
export interface ChangePasswordParams {
  oldPassword: string;
  newPassword: string;
}

// Token相关
export interface TokenPayload {
  sub: string; // 用户ID
  username: string;
  role: UserRole;
  exp: number; // 过期时间
}

// 权限相关
export interface Permission {
  canManageBoard: boolean;
  canConfigureWIP: boolean;
  canManageSprint: boolean;
  canViewAllTasks: boolean;
  canUpdateTask: (taskUserId: number) => boolean;
} 