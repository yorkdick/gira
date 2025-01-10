import { User, UserQueryParams, UserListResult, CreateUserParams, UpdateUserParams } from '@/types/user';
import request from '@/utils/request';

// 获取当前用户信息
export const getCurrentUser = (): Promise<User> => {
  return request.get<User>('/users/current').then(res => res.data);
};

// 获取用户列表
export const getUsers = (params: UserQueryParams): Promise<UserListResult> => {
  return request.get<UserListResult>('/users', { params }).then(res => res.data);
};

// 获取指定用户信息
export const getUser = (id: number): Promise<User> => {
  return request.get<User>(`/users/${id}`).then(res => res.data);
};

// 创建用户
export const createUser = (params: CreateUserParams): Promise<User> => {
  return request.post<User>('/users', params).then(res => res.data);
};

// 更新用户信息
export const updateUser = (id: number, params: UpdateUserParams): Promise<User> => {
  return request.put<User>(`/users/${id}`, params).then(res => res.data);
};

// 删除用户
export const deleteUser = (id: number): Promise<void> => {
  return request.delete<void>(`/users/${id}`).then(res => res.data);
}; 