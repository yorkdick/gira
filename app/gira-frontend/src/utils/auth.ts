import storage from './storage';

const TOKEN_KEY = 'token';
const USER_KEY = 'user';

interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  permissions: string[];
}

const auth = {
  // 保存token
  setToken(token: string): void {
    storage.set(TOKEN_KEY, token);
  },

  // 获取token
  getToken(): string | null {
    return storage.get(TOKEN_KEY);
  },

  // 删除token
  removeToken(): void {
    storage.remove(TOKEN_KEY);
  },

  // 保存用户信息
  setUser(user: User): void {
    storage.set(USER_KEY, user);
  },

  // 获取用户信息
  getUser(): User | null {
    return storage.get(USER_KEY);
  },

  // 删除用户信息
  removeUser(): void {
    storage.remove(USER_KEY);
  },

  // 清除所有认证信息
  clear(): void {
    this.removeToken();
    this.removeUser();
  },

  // 检查是否已登录
  isAuthenticated(): boolean {
    return !!this.getToken();
  },

  // 检查是否是管理员
  isAdmin(): boolean {
    const user = this.getUser();
    return user?.role === 'ADMIN';
  },

  // 检查是否有权限
  hasPermission(permission: string): boolean {
    const user = this.getUser();
    if (!user) return false;
    
    // 管理员拥有所有权限
    if (user.role === 'ADMIN') return true;

    // 检查用户是否具有指定权限
    const userPermissions = user.permissions || [];
    return userPermissions.includes(permission);
  }
};

export default auth; 