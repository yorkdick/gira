import React, { Suspense, useState } from 'react';
import { Layout, Menu, Spin, Avatar, Dropdown } from 'antd';
import type { MenuProps } from 'antd';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  UserOutlined,
  DashboardOutlined,
  TeamOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { logout } from '@/store/slices/authSlice';
import { RootState } from '@/store/types';
import styles from './BaseLayout.module.less';

const { Header, Sider, Content } = Layout;

/**
 * BaseLayout 组件 - 应用程序的主布局组件
 * 
 * 功能：
 * 1. 提供统一的页面布局结构，包括顶部导航栏、侧边栏和内容区域
 * 2. 处理响应式布局，在不同屏幕尺寸下自适应
 * 3. 管理用户认证状态和导航
 * 
 * @returns {JSX.Element} 返回一个包含完整布局结构的 React 组件
 */
const BaseLayout: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useDispatch();
  const { user } = useSelector((state: RootState) => state.auth);
  const [collapsed, setCollapsed] = useState(false);

  /**
   * 处理侧边栏折叠状态
   */
  const toggleCollapsed = () => {
    setCollapsed(!collapsed);
  };

  /**
   * 处理用户登出操作
   * 清除用户认证信息并跳转到登录页面
   */
  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  /**
   * 导航菜单配置
   * 根据用户角色显示不同的菜单项
   */
  const menuItems: MenuProps['items'] = [
    {
      key: '/board',
      icon: <DashboardOutlined />,
      label: '看板',
    },
    {
      key: '/sprints',
      icon: <MenuUnfoldOutlined />,
      label: 'Sprint',
    },
    user?.role === 'ADMIN' ? {
      key: '/users',
      icon: <TeamOutlined />,
      label: '用户管理',
    } : null,
    {
      key: '/settings',
      icon: <SettingOutlined />,
      label: '设置',
    },
  ].filter(Boolean);

  /**
   * 用户下拉菜单配置
   */
  const userMenuItems = [
    {
      key: 'settings',
      label: '个人设置',
      onClick: () => navigate('/settings'),
    },
    {
      key: 'logout',
      label: '退出登录',
      onClick: handleLogout,
    },
  ];

  return (
    <Layout className={styles.layout}>
      <Sider 
        trigger={null} 
        collapsible 
        collapsed={collapsed}
        className={styles.sider}
      >
        <div className={styles.logo} />
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header className={styles.header}>
          {React.createElement(
            collapsed ? MenuUnfoldOutlined : MenuFoldOutlined,
            {
              className: styles.trigger,
              onClick: toggleCollapsed,
            }
          )}
          <div className={styles.userInfo}>
            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
              <span className={styles.userAvatar}>
                <Avatar icon={<UserOutlined />} />
                <span className={styles.username}>{user?.username}</span>
              </span>
            </Dropdown>
          </div>
        </Header>
        <Content className={styles.content}>
          <Suspense fallback={<Spin size="large" className={styles.spin} />}>
            <Outlet />
          </Suspense>
        </Content>
      </Layout>
    </Layout>
  );
};

export default BaseLayout; 