import request from '@/utils/request';
import type { UserInfo } from '@/store/slices/authSlice';

interface LoginRequest {
  username: string;
  password: string;
}

interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

const authService = {
  login: (data: LoginRequest) => request.post<LoginResponse>('/auth/login', data),
  logout: () => request.post('/auth/logout'),
  getCurrentUser: () => request.get<UserInfo>('/users/current'),
  updateProfile: (data: Pick<UserInfo, 'username' | 'email' | 'fullName'>, id: string) =>
    request.put<UserInfo>(`/users/${id}`, data),
  updatePassword: (data: { oldPassword: string; newPassword: string }, id: string) =>
    request.put(`/users/${id}/password`, data),
  refreshToken: (refreshToken: string) =>
    request.post<LoginResponse>('/auth/refresh-token', { refreshToken }),
};

export default authService; 