/**
 * Login 页面组件 - 用户登录界面
 * 
 * 功能：
 * 1. 提供用户登录表单
 * 2. 处理登录请求和响应
 * 3. 管理登录状态和错误提示
 * 4. 登录成功后自动跳转
 * 
 * @component
 * @example
 * ```tsx
 * <Login />
 * ```
 */

import React from 'react';
import { Button, Card, Form, Input, message } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { setToken, setUser } from '@/store/slices/authSlice';
import authService from '@/services/authService';
import styles from './index.module.less';

interface LoginFormData {
  username: string;
  password: string;
}

interface LocationState {
  from?: {
    pathname: string;
  };
}

const Login: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useDispatch();
  const [loginForm] = Form.useForm<LoginFormData>();

  const handleSubmit = async (values: LoginFormData) => {
    try {
      // 获取登录响应
      const loginResponse = await authService.login(values);
      
      // 保存token
      dispatch(setToken(loginResponse.accessToken));
      
      try {
        // 获取用户信息
        const { data: userInfo } = await authService.getCurrentUser();
        
        if (!userInfo) {
          throw new Error('Failed to get user info');
        }
        
        // 保存用户信息
        dispatch(setUser(userInfo));

        message.success('登录成功');
        
        // 获取之前尝试访问的页面路径，如果没有则默认跳转到看板页面
        const locationState = location.state as LocationState;
        const from = locationState?.from?.pathname || '/board';
        navigate(from, { replace: true });
      } catch (error) {
        console.error('Failed to get user info:', error);
        message.error('获取用户信息失败');
        // 清除token
        dispatch(setToken(null));
        // 重置密码字段
        loginForm.resetFields(['password']);
      }
    } catch (error) {
      console.error('Login error:', error);
      message.error('登录失败，请检查用户名和密码');
      // 只重置密码字段
      loginForm.resetFields(['password']);
    }
  };

  return (
    <div className={styles.container}>
      <Card title="GIRA 项目管理系统" className={styles.card}>
        <Form
          name="loginForm"
          form={loginForm}
          onFinish={handleSubmit}
          autoComplete="off"
          layout="vertical"
          requiredMark={false}
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="用户名"
              size="large"
              autoComplete="username"
            />
          </Form.Item>
          <Form.Item
            name="password"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="密码"
              size="large"
              autoComplete="current-password"
            />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block size="large">
              登录
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default Login; 