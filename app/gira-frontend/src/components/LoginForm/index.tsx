import React from 'react';
import { Form, Input, Button, message } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { LoginParams } from '@/types/auth';
import { login } from '@/services/auth';
import { setToken } from '@/store/slices/authSlice';
import styles from './style.module.less';

const LoginForm: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useDispatch();
  const [loading, setLoading] = React.useState(false);
  const [form] = Form.useForm();

  const handleSubmit = async (values: LoginParams) => {
    if (!values.username || !values.password) {
      message.error('请输入用户名和密码');
      return;
    }

    try {
      setLoading(true);
      const result = await login(values);
      
      if (!result || !result.accessToken) {
        throw new Error('登录响应缺少accessToken');
      }

      dispatch(setToken(result.accessToken));
      message.success('登录成功');
      
      // 获取重定向路径
      const from = (location.state as any)?.from || '/';
      navigate(from, { replace: true });
    } catch (error: any) {
      console.error('Login error:', error);
      message.error('登录失败，请检查用户名和密码');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Form
      form={form}
      name="login"
      onFinish={handleSubmit}
      className={styles.form}
      size="large"
      initialValues={{ remember: true }}
    >
      <Form.Item
        name="username"
        rules={[{ required: true, message: '请输入用户名' }]}
      >
        <Input
          prefix={<UserOutlined />}
          placeholder="用户名"
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
          autoComplete="current-password"
        />
      </Form.Item>

      <Form.Item>
        <Button
          type="primary"
          htmlType="submit"
          loading={loading}
          block
        >
          登录
        </Button>
      </Form.Item>
    </Form>
  );
};

export default LoginForm; 