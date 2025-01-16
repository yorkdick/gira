import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  Table,
  Button,
  Space,
  Tag,
  Modal,
  message,
  Typography,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons';
import { AppDispatch, RootState } from '@/store';
import { UserInfo } from '@/store/slices/authSlice';
import { fetchUsers, deleteUser } from '@/store/slices/userSlice';
import UserForm from './components/UserForm';
import styles from './index.module.less';

const { Title } = Typography;
const { confirm } = Modal;

const UsersPage: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { user, loading } = useSelector((state: RootState) => state.auth);
  const { list: users } = useSelector((state: RootState) => state.user);
  const isAdmin = user?.role === 'ADMIN';

  const [formVisible, setFormVisible] = useState(false);
  const [editingUser, setEditingUser] = useState<UserInfo | null>(null);

  useEffect(() => {
    dispatch(fetchUsers());
  }, [dispatch]);

  const handleCreateUser = () => {
    setEditingUser(null);
    setFormVisible(true);
  };

  const handleEditUser = (user: UserInfo) => {
    setEditingUser(user);
    setFormVisible(true);
  };

  const handleDeleteUser = (userId: string) => {
    confirm({
      title: '确认删除',
      icon: <ExclamationCircleOutlined />,
      content: '确定要删除这个用户吗？删除后无法恢复。',
      okText: '确定',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await dispatch(deleteUser(userId)).unwrap();
        } catch (error) {
          if (error instanceof Error) {
            message.error(error.message);
          } else {
            message.error('删除用户失败');
          }
        }
      },
    });
  };

  const columns = [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: '角色',
      dataIndex: 'role',
      key: 'role',
      render: (role: UserInfo['role']) => (
        <Tag color={role === 'ADMIN' ? 'red' : 'blue'}>
          {role === 'ADMIN' ? '管理员' : '普通用户'}
        </Tag>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date: string) => new Date(date).toLocaleDateString(),
    },
    {
      title: '操作',
      key: 'action',
      render: (_: unknown, record: UserInfo) => (
        <Space size="middle">
          <Button
            type="text"
            icon={<EditOutlined />}
            onClick={() => handleEditUser(record)}
          >
            编辑
          </Button>
          {isAdmin && record.id !== user?.id && (
            <Button
              type="text"
              danger
              icon={<DeleteOutlined />}
              onClick={() => handleDeleteUser(record.id)}
            >
              删除
            </Button>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <Title level={2}>用户管理</Title>
        {isAdmin && (
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={handleCreateUser}
          >
            创建用户
          </Button>
        )}
      </div>

      <Table
        columns={columns}
        dataSource={users}
        loading={loading}
        rowKey="id"
        pagination={{
          defaultPageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
        }}
      />

      <UserForm
        visible={formVisible}
        initialValues={editingUser}
        onCancel={() => {
          setFormVisible(false);
          setEditingUser(null);
        }}
      />
    </div>
  );
};

export default UsersPage; 