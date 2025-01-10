import { LoginParams, LoginResult, RegisterParams } from '@/types/auth';
import request from '@/utils/request';

// 登录服务
export const login = async (params: LoginParams): Promise<LoginResult> => {
  const response = await request.post<LoginResult>('/auth/login', params);
  return response.data;
};

// 注册服务
export const register = async (params: RegisterParams): Promise<void> => {
  const response = await request.post<void>('/auth/register', params);
  return response.data;
};

// 登出服务
export const logout = async (): Promise<void> => {
  const response = await request.post<void>('/auth/logout');
  return response.data;
}; 