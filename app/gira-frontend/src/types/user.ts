// 用户基本信息
export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  permissions?: string[];
  avatar?: string;
  createdAt?: string;
  updatedAt?: string;
}

// 用户列表查询参数
export interface UserQueryParams {
  page?: number;
  pageSize?: number;
  keyword?: string;
  role?: string;
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
  role: string;
  permissions?: string[];
}

// 更新用户参数
export interface UpdateUserParams {
  email?: string;
  role?: string;
  permissions?: string[];
  avatar?: string;
}

// 用户状态
export enum UserStatus {
  ACTIVE = 'active',
  INACTIVE = 'inactive',
  LOCKED = 'locked'
}

// 用户角色
export enum UserRole {
  ADMIN = 'ADMIN',
  DEVELOPER = 'DEVELOPER'
} 