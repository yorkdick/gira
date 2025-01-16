import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Form, Input, Select, Modal, message } from 'antd';
import { AppDispatch, RootState } from '@/store';
import { UserInfo } from '@/store/slices/authSlice';
import { createUser, updateUser } from '@/store/slices/userSlice';
import type { UserCreateDTO, UserUpdateDTO } from '@/store/slices/userSlice';
import styles from './index.module.less';

const { Option } = Select;

interface UserFormProps {
  visible: boolean;
  initialValues?: UserInfo | null;
  onCancel: () => void;
}

const UserForm: React.FC<UserFormProps> = ({
  visible,
  initialValues,
  onCancel,
}) => {
  const [form] = Form.useForm();
  const dispatch = useDispatch<AppDispatch>();
  const { loading } = useSelector((state: RootState) => state.auth);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (initialValues) {
        await dispatch(updateUser({
          id: initialValues.id,
          data: {
            username: values.username,
            email: values.email,
            role: values.role,
          } as UserUpdateDTO,
        })).unwrap();
      } else {
        await dispatch(createUser({
          username: values.username,
          email: values.email,
          password: values.password,
          role: values.role,
        } as UserCreateDTO)).unwrap();
      }
      form.resetFields();
      onCancel();
    } catch (error) {
      if (error instanceof Error) {
        message.error(error.message);
      } else {
        message.error('提交失败，请检查表单');
      }
    }
  };

  React.useEffect(() => {
    if (visible && initialValues) {
      form.setFieldsValue({
        username: initialValues.username,
        email: initialValues.email,
        role: initialValues.role,
      });
    } else {
      form.resetFields();
    }
  }, [visible, initialValues, form]);

  return (
    <Modal
      title={initialValues ? '编辑用户' : '创建用户'}
      open={visible}
      onOk={handleSubmit}
      onCancel={onCancel}
      confirmLoading={loading}
      destroyOnClose
    >
      <Form
        form={form}
        layout="vertical"
        className={styles.form}
        initialValues={{
          username: '',
          email: '',
          role: 'USER' as const,
        }}
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

        {!initialValues && (
          <Form.Item
            name="password"
            label="密码"
            rules={[
              { required: true, message: '请输入密码' },
              { min: 6, message: '密码至少6个字符' },
            ]}
          >
            <Input.Password placeholder="请输入密码" />
          </Form.Item>
        )}

        <Form.Item
          name="role"
          label="角色"
          rules={[{ required: true, message: '请选择角色' }]}
        >
          <Select placeholder="请选择角色">
            <Option value="USER">普通用户</Option>
            <Option value="ADMIN">管理员</Option>
          </Select>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default UserForm; 