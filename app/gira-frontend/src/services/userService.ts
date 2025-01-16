import request from '@/utils/request';
import type { UserInfo } from '@/store/slices/authSlice';
import type { UserCreateDTO } from '@/store/slices/userSlice';

export interface UserStatus {
  id: string;
  status: 'ACTIVE' | 'INACTIVE' | 'BLOCKED';
  lastLoginAt?: string;
  lastActivityAt?: string;
}

const userService = {
  getUsers: () => request.get<UserInfo[]>('/users'),
  getUser: (id: string) => request.get<UserInfo>(`/users/${id}`),
  createUser: (data: UserCreateDTO) => request.post<UserInfo>('/users', data),
  updateUser: (id: string, data: Partial<UserInfo>) => request.put<UserInfo>(`/users/${id}`, data),
  deleteUser: (id: string) => request.delete(`/users/${id}`),
  getUserStatus: (id: string) => request.get<UserStatus>(`/users/${id}/status`),
  activateUser: (id: string) => request.put<UserStatus>(`/users/${id}/activate`),
  deactivateUser: (id: string) => request.put<UserStatus>(`/users/${id}/deactivate`),
  blockUser: (id: string) => request.put<UserStatus>(`/users/${id}/block`),
  unblockUser: (id: string) => request.put<UserStatus>(`/users/${id}/unblock`),
  getUserActivity: (id: string) =>
    request.get<{
      lastLoginAt?: string;
      lastActivityAt?: string;
      totalTasks: number;
      completedTasks: number;
      inProgressTasks: number;
    }>(`/users/${id}/activity`),
  updatePassword: (id: string, data: { oldPassword: string; newPassword: string }) =>
    request.put(`/users/${id}/password`, data),
};

export default userService; 