import React, { useState } from 'react';
import { Form, Input, Button, Card, message } from 'antd';
import { LockOutlined } from '@ant-design/icons';
import { useNavigate, useSearchParams } from 'react-router-dom';
import authService from '../../services/auth.service';
import './ResetPassword.css';

interface ResetPasswordFormData {
  password: string;
  confirmPassword: string;
}

const ResetPassword: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');

  const onFinish = async (values: ResetPasswordFormData) => {
    if (!token) {
      message.error('无效的重置链接');
      return;
    }

    try {
      setLoading(true);
      // TODO: Implement reset password functionality
      message.success('密码重置成功');
      navigate('/login');
    } catch (error: any) {
      message.error(error.response?.data?.message || '密码重置失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="reset-password-container">
      <Card title="重置密码" className="reset-password-card">
        <Form
          name="reset-password"
          onFinish={onFinish}
          size="large"
        >
          <Form.Item
            name="password"
            rules={[
              { required: true, message: '请输入新密码' },
              { min: 6, message: '密码至少6个字符' }
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="新密码"
              autoComplete="new-password"
            />
          </Form.Item>

          <Form.Item
            name="confirmPassword"
            dependencies={['password']}
            rules={[
              { required: true, message: '请确认新密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次输入的密码不一致'));
                },
              }),
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="确认新密码"
              autoComplete="new-password"
            />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} block>
              重置密码
            </Button>
          </Form.Item>

          <div className="reset-password-links">
            <a href="/login">返回登录</a>
          </div>
        </Form>
      </Card>
    </div>
  );
};

export default ResetPassword; 