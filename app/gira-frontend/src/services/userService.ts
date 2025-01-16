import request from '@/utils/request';
import type { UserInfo } from '@/store/slices/authSlice';

export interface UserStatus {
  id: string;
  status: 'ACTIVE' | 'INACTIVE' | 'BLOCKED';
  lastLoginAt?: string;
  lastActivityAt?: string;
}

const userService = {
  getUsers: () => request.get<UserInfo[]>('/api/users'),
  getUser: (id: string) => request.get<UserInfo>(`/api/users/${id}`),
  createUser: (data: Omit<UserInfo, 'id'>) => request.post<UserInfo>('/api/users', data),
  updateUser: (id: string, data: Partial<UserInfo>) => request.put<UserInfo>(`/api/users/${id}`, data),
  deleteUser: (id: string) => request.delete(`/api/users/${id}`),
  getUserStatus: (id: string) => request.get<UserStatus>(`/api/users/${id}/status`),
  activateUser: (id: string) => request.put<UserStatus>(`/api/users/${id}/activate`),
  deactivateUser: (id: string) => request.put<UserStatus>(`/api/users/${id}/deactivate`),
  blockUser: (id: string) => request.put<UserStatus>(`/api/users/${id}/block`),
  unblockUser: (id: string) => request.put<UserStatus>(`/api/users/${id}/unblock`),
  getUserActivity: (id: string) =>
    request.get<{
      lastLoginAt?: string;
      lastActivityAt?: string;
      totalTasks: number;
      completedTasks: number;
      inProgressTasks: number;
    }>(`/api/users/${id}/activity`),
  updatePassword: (id: string, data: { oldPassword: string; newPassword: string }) =>
    request.put(`/api/users/${id}/password`, data),
};

export default userService; 