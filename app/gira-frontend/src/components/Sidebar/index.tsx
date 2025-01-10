import React from 'react';
import { Layout, Menu } from 'antd';
import {
  DashboardOutlined,
  ProjectOutlined,
  UnorderedListOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import ProjectList from '@/components/ProjectList';
import QuickActions from '@/components/QuickActions';
import styles from './style.module.less';

const { Sider } = Layout;

const Sidebar: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const menuItems = [
    {
      key: '/',
      icon: <DashboardOutlined />,
      label: '仪表盘',
    },
    {
      key: '/projects',
      icon: <ProjectOutlined />,
      label: '项目',
    },
    {
      key: '/backlog',
      icon: <UnorderedListOutlined />,
      label: 'Backlog',
    },
    {
      key: '/settings',
      icon: <SettingOutlined />,
      label: '设置',
    },
  ];

  const handleMenuClick = ({ key }: { key: string }) => {
    navigate(key);
  };

  return (
    <Sider width={256} className={styles.sidebar}>
      <div className={styles.sidebarContent}>
        <div className={styles.menuContainer}>
          <Menu
            mode="inline"
            selectedKeys={[location.pathname]}
            items={menuItems}
            onClick={handleMenuClick}
          />
        </div>
        <div className={styles.projectList}>
          <ProjectList />
        </div>
        <div className={styles.quickActions}>
          <QuickActions />
        </div>
      </div>
    </Sider>
  );
};

export default Sidebar; 