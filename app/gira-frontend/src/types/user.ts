// 用户基本信息
export enum UserRole {
  ADMIN = 'ADMIN',
  MANAGER = 'MANAGER',
  USER = 'USER',
}

export interface User {
  id: number;
  username: string;
  name: string;
  email: string;
  avatar?: string;
  role: UserRole;
  createdAt: string;
  updatedAt: string;
}

// 用户列表查询参数
export interface UserQueryParams {
  page?: number;
  pageSize?: number;
  keyword?: string;
  role?: UserRole;
}

// 用户列表响应数据
export interface UserListResult {
  items: User[];
  total: number;
  page: number;
  pageSize: number;
}

// 创建用户参数
export interface CreateUserParams {
  username: string;
  name: string;
  email: string;
  password: string;
  role: UserRole;
}

// 更新用户参数
export interface UpdateUserParams {
  name?: string;
  email?: string;
  avatar?: string;
  role?: UserRole;
}

export interface UpdatePasswordParams {
  oldPassword: string;
  newPassword: string;
}

// 用户状态
export enum UserStatus {
  ACTIVE = 'active',
  INACTIVE = 'inactive',
  LOCKED = 'locked'
} 