import React, { useState } from 'react';
import { Form, Input, Button, Card, message } from 'antd';
import { MailOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import authService from '../../services/auth.service';
import './ForgotPassword.css';

interface ForgotPasswordFormData {
  email: string;
}

const ForgotPassword: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const onFinish = async (values: ForgotPasswordFormData) => {
    try {
      setLoading(true);
      // TODO: Implement forgot password functionality
      message.success('重置密码链接已发送到您的邮箱');
      navigate('/login');
    } catch (error: any) {
      message.error(error.response?.data?.message || '发送重置密码链接失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="forgot-password-container">
      <Card title="忘记密码" className="forgot-password-card">
        <p className="forgot-password-description">
          请输入您的注册邮箱，我们将向您发送重置密码的��接。
        </p>
        <Form
          name="forgot-password"
          onFinish={onFinish}
          size="large"
        >
          <Form.Item
            name="email"
            rules={[
              { required: true, message: '请输入邮箱' },
              { type: 'email', message: '请输入有效的邮箱地址' }
            ]}
          >
            <Input
              prefix={<MailOutlined />}
              placeholder="邮箱"
              autoComplete="email"
            />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} block>
              发送重置链接
            </Button>
          </Form.Item>

          <div className="forgot-password-links">
            <a href="/login">返回登录</a>
          </div>
        </Form>
      </Card>
    </div>
  );
};

export default ForgotPassword; 