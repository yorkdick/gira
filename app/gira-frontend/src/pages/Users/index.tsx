import React, { useEffect, useState } from 'react';
import { Table, Tag, Button } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import type { RootState, AppDispatch } from '@/store';
import { fetchUsers } from '@/store/slices/userSlice';
import type { UserInfo } from '@/store/slices/authSlice';
import UserForm from './components/UserForm';

const UsersPage: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { list: users, loading } = useSelector((state: RootState) => state.users);
  const [formVisible, setFormVisible] = useState(false);
  const [editingUser, setEditingUser] = useState<UserInfo | null>(null);

  useEffect(() => {
    dispatch(fetchUsers());
  }, [dispatch]);

  const handleRowClick = (record: UserInfo) => {
    setEditingUser(record);
    setFormVisible(true);
  };

  const handleFormClose = () => {
    setFormVisible(false);
    setEditingUser(null);
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
        <Tag color={role === 'ADMIN' ? 'red' : 'blue'}>{role}</Tag>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: UserInfo['status']) => (
        <Tag color={status === 'ACTIVE' ? 'green' : 'red'}>{status}</Tag>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <div style={{ marginBottom: '16px', textAlign: 'right' }}>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => setFormVisible(true)}
        >
          创建用户
        </Button>
      </div>
      <Table
        columns={columns}
        dataSource={users}
        loading={loading}
        rowKey="id"
        onRow={(record) => ({
          onClick: () => handleRowClick(record),
          style: { cursor: 'pointer' },
        })}
      />
      <UserForm
        visible={formVisible}
        initialValues={editingUser}
        onCancel={handleFormClose}
      />
    </div>
  );
};

export default UsersPage; 