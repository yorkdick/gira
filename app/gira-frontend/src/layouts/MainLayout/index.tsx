import React from 'react';
import { Layout } from 'antd';
import { Outlet } from 'react-router-dom';
import Header from '@/components/Header';
import Sidebar from '@/components/Sidebar';
import styles from './style.module.less';

const { Content } = Layout;

const MainLayout: React.FC = () => {
  return (
    <Layout className={styles.layout}>
      <Header />
      <Layout>
        <Sidebar />
        <Content className={styles.content}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};

export default MainLayout; 