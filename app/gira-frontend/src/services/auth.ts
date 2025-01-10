import { LoginParams, LoginResult, RegisterParams } from '@/types/auth';
import request from '@/utils/request';

// 登录服务
export const login = (params: LoginParams): Promise<LoginResult> => {
  return request.post<LoginResult>('/auth/login', params).then(res => res.data);
};

// 注册服务
export const register = (params: RegisterParams): Promise<void> => {
  return request.post<void>('/auth/register', params).then(res => res.data);
};

// 登出服务
export const logout = (): Promise<void> => {
  return request.post<void>('/auth/logout').then(res => res.data);
}; 