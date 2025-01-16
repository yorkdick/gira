import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  Card,
  Avatar,
  Typography,
  Form,
  Input,
  Button,
  Divider,
  message,
} from 'antd';
import { UserOutlined } from '@ant-design/icons';
import { AppDispatch, RootState } from '@/store';
import { updateProfile, updatePassword } from '@/store/slices/authSlice';
import styles from './index.module.less';

const { Title } = Typography;

interface ProfileFormData {
  username: string;
  email: string;
  avatar?: string;
}

interface PasswordFormData {
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
}

const SettingsPage: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { user, loading } = useSelector((state: RootState) => state.auth);
  const [profileForm] = Form.useForm();
  const [passwordForm] = Form.useForm();

  const handleUpdateProfile = async (values: ProfileFormData) => {
    try {
      await dispatch(updateProfile(values)).unwrap();
      profileForm.resetFields();
    } catch (error) {
      if (error instanceof Error) {
        message.error(error.message);
      } else {
        message.error('更新个人信息失败');
      }
    }
  };

  const handleUpdatePassword = async (values: PasswordFormData) => {
    try {
      if (values.newPassword !== values.confirmPassword) {
        message.error('两次输入的密码不一致');
        return;
      }
      await dispatch(updatePassword({
        oldPassword: values.oldPassword,
        newPassword: values.newPassword,
      })).unwrap();
      passwordForm.resetFields();
    } catch (error) {
      if (error instanceof Error) {
        message.error(error.message);
      } else {
        message.error('修改密码失败');
      }
    }
  };

  return (
    <div className={styles.container}>
      <Title level={2}>个人设置</Title>

      <Card className={styles.card}>
        <div className={styles.profile}>
          <Avatar
            size={80}
            icon={<UserOutlined />}
            src={user?.avatar}
            className={styles.avatar}
          />
          <div className={styles.info}>
            <Title level={4}>{user?.username}</Title>
            <span>{user?.email}</span>
          </div>
        </div>

        <Divider />

        <Title level={3}>个人信息</Title>
        <Form
          form={profileForm}
          layout="vertical"
          initialValues={{
            username: user?.username,
            email: user?.email,
            avatar: user?.avatar,
          }}
          onFinish={handleUpdateProfile}
        >
          <Form.Item
            name="username"
            label="用户名"
            rules={[
              { required: true, message: '请输入用户名' },
              { min: 3, message: '用户名至少3个字符' },
            ]}
          >
            <Input placeholder="请输入用户名" />
          </Form.Item>

          <Form.Item
            name="email"
            label="邮箱"
            rules={[
              { required: true, message: '请输入邮箱' },
              { type: 'email', message: '请输入有效的邮箱地址' },
            ]}
          >
            <Input placeholder="请输入邮箱" />
          </Form.Item>

          <Form.Item
            name="avatar"
            label="头像URL"
          >
            <Input placeholder="请输入头像URL" />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading}>
              更新信息
            </Button>
          </Form.Item>
        </Form>

        <Divider />

        <Title level={3}>修改密码</Title>
        <Form
          form={passwordForm}
          layout="vertical"
          onFinish={handleUpdatePassword}
        >
          <Form.Item
            name="oldPassword"
            label="当前密码"
            rules={[
              { required: true, message: '请输入当前密码' },
              { min: 6, message: '密码至少6个字符' },
            ]}
          >
            <Input.Password placeholder="请输入当前密码" />
          </Form.Item>

          <Form.Item
            name="newPassword"
            label="新密码"
            rules={[
              { required: true, message: '请输入新密码' },
              { min: 6, message: '密码至少6个字符' },
            ]}
          >
            <Input.Password placeholder="请输入新密码" />
          </Form.Item>

          <Form.Item
            name="confirmPassword"
            label="确认新密码"
            rules={[
              { required: true, message: '请确认新密码' },
              { min: 6, message: '密码至少6个字符' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('newPassword') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次输入的密码不一致'));
                },
              }),
            ]}
          >
            <Input.Password placeholder="请确认新密码" />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading}>
              修改密码
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default SettingsPage; 