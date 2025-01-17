import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Form, Input, Select, Modal, message, Button } from 'antd';
import { DeleteOutlined } from '@ant-design/icons';
import { AppDispatch, RootState } from '@/store';
import { UserInfo } from '@/store/slices/authSlice';
import { createUser, updateUser, deleteUser } from '@/store/slices/userSlice';
import type { UserCreateDTO, UserUpdateDTO } from '@/store/slices/userSlice';
import styles from './index.module.less';

const { Option } = Select;
const { confirm } = Modal;

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
  const { user: currentUser } = useSelector((state: RootState) => state.auth);

  const handleDelete = async () => {
    if (!initialValues) return;
    
    confirm({
      title: '确认删除',
      content: '确定要删除这个用户吗？此操作不可恢复。',
      okText: '确定',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await dispatch(deleteUser(initialValues.id)).unwrap();
          message.success('删除成功');
          onCancel();
        } catch (error) {
          if (error instanceof Error) {
            message.error(error.message);
          } else {
            message.error('删除失败');
          }
        }
      },
    });
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (initialValues) {
        await dispatch(updateUser({
          id: initialValues.id,
          data: {
            username: values.username,
            email: values.email,
            fullName: values.fullName,
            role: values.role,
          } as UserUpdateDTO,
        })).unwrap();
      } else {
        await dispatch(createUser({
          username: values.username,
          email: values.email,
          password: values.password,
          fullName: values.fullName,
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
        fullName: initialValues.fullName,
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
      footer={[
        initialValues && currentUser?.role === 'ADMIN' && initialValues.id !== currentUser?.id && (
          <Button
            key="delete"
            type="primary"
            danger
            icon={<DeleteOutlined />}
            onClick={handleDelete}
          >
            删除
          </Button>
        ),
        <Button key="cancel" onClick={onCancel}>
          取消
        </Button>,
        <Button key="submit" type="primary" loading={loading} onClick={handleSubmit}>
          确定
        </Button>,
      ].filter(Boolean)}
    >
      <Form
        form={form}
        layout="vertical"
        className={styles.form}
        initialValues={{
          username: '',
          email: '',
          fullName: '',
          role: 'DEVELOPER' as const,
        }}
      >
        <Form.Item
          name="username"
          label="用户名"
          rules={[
            { required: true, message: '请输入用户名' },
            { min: 3, max: 50, message: '用户名长度必须在3-50之间' },
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
          name="fullName"
          label="全名"
          rules={[
            { required: true, message: '请输入全名' },
          ]}
        >
          <Input placeholder="请输入全名" />
        </Form.Item>

        {!initialValues && (
          <Form.Item
            name="password"
            label="密码"
            rules={[
              { required: true, message: '请输入密码' },
              { min: 6, message: '密码长度不能小于6位' },
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
            <Option value="DEVELOPER">开发者</Option>
            <Option value="ADMIN">管理员</Option>
          </Select>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default UserForm; 