// 用户基本信息
export enum UserRole {
  ADMIN = 'ADMIN',
  DEVELOPER = 'DEVELOPER',
}

export interface User {
  id: number;
  name: string;
  email: string;
  role: UserRole;
  avatar?: string;
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
  email: string;
  password: string;
  role: UserRole;
  avatar?: string;
}

// 更新用户参数
export interface UpdateUserParams {
  username?: string;
  email?: string;
  password?: string;
  role?: UserRole;
  avatar?: string;
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

export interface LoginParams {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: User;
} 