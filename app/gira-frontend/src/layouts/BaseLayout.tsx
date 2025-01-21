import React, { Suspense, useEffect } from 'react';
import { Layout, Menu, Spin, Avatar, Dropdown, Space, Typography, Breadcrumb } from 'antd';
import type { MenuProps } from 'antd';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import {
  UserOutlined,
  DashboardOutlined,
  TeamOutlined,
  BugOutlined,
  RightOutlined,
} from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { logout } from '@/store/slices/authSlice';
import { RootState } from '@/store/types';
import styles from './BaseLayout.module.less';

const { Header, Content, Sider } = Layout;
const { Title } = Typography;

// 菜单配置
const MENU_CONFIG = {
  '/board': { icon: <DashboardOutlined />, label: '看板' },
  '/sprints': { icon: <RightOutlined />, label: 'Sprint' },
  '/users': { icon: <TeamOutlined />, label: '用户管理' },
} as const;

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

  // 获取当前页面标题
  const getCurrentPageTitle = () => {
    const path = location.pathname;
    return MENU_CONFIG[path as keyof typeof MENU_CONFIG]?.label || '';
  };

  // 更新浏览器标签页标题
  useEffect(() => {
    const pageTitle = getCurrentPageTitle();
    document.title = pageTitle ? `${pageTitle} - Gira` : 'Gira';
  }, [location.pathname]);

  /**
   * 处理用户登出操作
   */
  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  /**
   * 导航菜单配置
   */
  const menuItems: MenuProps['items'] = [
    {
      key: '/board',
      icon: <DashboardOutlined />,
      label: '看板',
    },
    {
      key: '/sprints',
      icon: <RightOutlined />,
      label: 'Sprint',
    },
    user?.role === 'ADMIN' ? {
      key: '/users',
      icon: <TeamOutlined />,
      label: '用户管理',
    } : null,
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
      <Header className={styles.header}>
        <div className={styles.headerLeft}>
          <Space size="middle">
            <BugOutlined className={styles.logo} />
            <Title level={4} style={{ margin: 0, color: '#fff' }}>GIRA</Title>
          </Space>
        </div>
        <div className={styles.headerNav}>
          <Breadcrumb
            items={[
              { title: 'GIRA' },
              { title: getCurrentPageTitle() }
            ]}
          />
        </div>
        <div className={styles.userInfo}>
          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
            <span className={styles.userAvatar}>
              <Avatar icon={<UserOutlined />} />
              <span className={styles.username}>{user?.username}</span>
            </span>
          </Dropdown>
        </div>
      </Header>
      <Layout>
        <Sider width={200} className={styles.sider}>
          <Menu
            mode="inline"
            selectedKeys={[location.pathname]}
            items={menuItems}
            onClick={({ key }) => navigate(key)}
            className={styles.menu}
          />
        </Sider>
        <Layout>
          <Content className={styles.content}>
            <div className={styles.pageHeader}>
              <Title level={3}>{getCurrentPageTitle()}</Title>
            </div>
            <div className={styles.pageContent}>
              <Suspense fallback={<Spin size="large" className={styles.spin} />}>
                <Outlet />
              </Suspense>
            </div>
          </Content>
        </Layout>
      </Layout>
    </Layout>
  );
};

export default BaseLayout; 