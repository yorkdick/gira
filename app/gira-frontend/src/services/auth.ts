import { LoginParams, LoginResult, RegisterParams } from '@/types/auth';
import { http } from '@/utils/request';

// 登录服务
export const login = async (params: LoginParams): Promise<LoginResult> => {
  return http.post('auth/login', params);
};

// 注册服务
export const register = async (params: RegisterParams): Promise<void> => {
  return http.post('auth/register', params);
};

// 登出服务
export const logout = async (): Promise<void> => {
  return http.post('auth/logout');
}; 