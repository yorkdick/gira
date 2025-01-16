import request from '@/utils/request';
import type { UserInfo } from '@/store/slices/authSlice';

export interface LoginResponse {
  accessToken: string;
  user: UserInfo;
}

const authService = {
  login: async (data: { username: string; password: string }) => {
    const response = await request.post<LoginResponse>('/auth/login', data);
    return response.data;
  },
  logout: () => request.post('/auth/logout'),
  getCurrentUser: () => request.get<UserInfo>('/users/current'),
  updateProfile: (data: Pick<UserInfo, 'username' | 'email' | 'avatar'>) =>
    request.put<UserInfo>('/users/profile', data),
  updatePassword: (data: { oldPassword: string; newPassword: string }) =>
    request.put('/users/password', data),
};

export default authService; 