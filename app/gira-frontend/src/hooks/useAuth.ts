import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { RootState } from '@/store';
import { setToken, clearToken } from '@/store/slices/authSlice';
import { setCurrentUser, clearCurrentUser } from '@/store/slices/userSlice';
import { login as loginService, logout as logoutService } from '@/services/auth';
import { getCurrentUser } from '@/services/user';
import { LoginParams } from '@/types/auth';
import { message } from 'antd';

export const useAuth = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const token = useSelector((state: RootState) => state.auth.token);
  const currentUser = useSelector((state: RootState) => state.user.currentUser);

  // 检查是否已认证
  const isAuthenticated = Boolean(token);

  // 登录
  const login = async (params: LoginParams) => {
    try {
      const result = await loginService(params);
      dispatch(setToken(result.token));
      
      // 获取并保存用户信息
      const userInfo = await getCurrentUser();
      dispatch(setCurrentUser(userInfo));
      
      message.success('登录成功');
      navigate('/');
      return true;
    } catch {
      message.error('登录失败，请检查用户名和密码');
      return false;
    }
  };

  // 登出
  const logout = async () => {
    try {
      await logoutService();
      dispatch(clearToken());
      dispatch(clearCurrentUser());
      navigate('/login');
      message.success('已退出登录');
    } catch {
      message.error('退出登录失败');
    }
  };

  // 检查用户是否有特定权限
  const hasPermission = (permission: string) => {
    if (!currentUser) return false;
    return currentUser.role === 'ADMIN' || currentUser.permissions?.includes(permission);
  };

  // 检查用户是否有特定角色
  const hasRole = (role: string) => {
    if (!currentUser) return false;
    return currentUser.role === role;
  };

  return {
    isAuthenticated,
    currentUser,
    login,
    logout,
    hasPermission,
    hasRole,
  };
}; 