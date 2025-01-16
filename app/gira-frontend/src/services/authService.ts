import request from '@/utils/request';
import type { UserInfo } from '@/store/slices/authSlice';

export interface LoginResponse {
  token: string;
  user: UserInfo;
}

const authService = {
  login: (data: { username: string; password: string }) =>
    request.post<LoginResponse>('/api/auth/login', data),
  logout: () => request.post('/api/auth/logout'),
  getCurrentUser: () => request.get<UserInfo>('/api/users/current'),
  updateProfile: (data: Pick<UserInfo, 'username' | 'email' | 'avatar'>) =>
    request.put<UserInfo>('/api/users/profile', data),
  updatePassword: (data: { oldPassword: string; newPassword: string }) =>
    request.put('/api/users/password', data),
};

export default authService; 