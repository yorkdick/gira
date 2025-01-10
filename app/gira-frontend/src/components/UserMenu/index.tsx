import React from 'react';
import { Avatar, Dropdown } from 'antd';
import { UserOutlined, LogoutOutlined, SettingOutlined } from '@ant-design/icons';
import { useAuth } from '@/hooks/useAuth';
import styles from './style.module.less';

const UserMenu: React.FC = () => {
  const { currentUser, logout } = useAuth();

  const items = [
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '个人设置',
    },
    {
      type: 'divider' as const,
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: logout,
    },
  ];

  return (
    <Dropdown menu={{ items }} placement="bottomRight">
      <div className={styles.userMenu}>
        <Avatar
          size="small"
          icon={<UserOutlined />}
          src={currentUser?.avatar}
          alt={currentUser?.username}
        />
        <span className={styles.username}>{currentUser?.username}</span>
      </div>
    </Dropdown>
  );
};

export default UserMenu; 