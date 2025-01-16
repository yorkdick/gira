// Token相关的常量
const TOKEN_KEY = 'token';
const USER_KEY = 'user';

// Token管理
export const getToken = (): string | null => {
  return localStorage.getItem(TOKEN_KEY);
};

export const setToken = (token: string): void => {
  localStorage.setItem(TOKEN_KEY, token);
};

export const removeToken = (): void => {
  localStorage.removeItem(TOKEN_KEY);
};

// 用户信息管理
export interface UserInfo {
  id: string;
  username: string;
  email: string;
  fullName: string;
  role: 'ADMIN' | 'DEVELOPER';
  status: 'ACTIVE' | 'INACTIVE';
}

export const getCurrentUser = (): UserInfo | null => {
  const userStr = localStorage.getItem(USER_KEY);
  return userStr ? JSON.parse(userStr) : null;
};

export const setCurrentUser = (user: UserInfo): void => {
  localStorage.setItem(USER_KEY, JSON.stringify(user));
};

export const removeCurrentUser = (): void => {
  localStorage.removeItem(USER_KEY);
};

// 清除所有认证信息
export const clearAuth = (): void => {
  removeToken();
  removeCurrentUser();
};

// 检查是否已登录
export const isAuthenticated = (): boolean => {
  return !!getToken() && !!getCurrentUser();
};

// 检查是否是管理员
export const isAdmin = (): boolean => {
  const user = getCurrentUser();
  return user?.role === 'ADMIN';
}; 