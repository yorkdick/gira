import React from 'react';
import { Layout } from 'antd';
import { Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { selectIsAuthenticated } from '@/store/slices/authSlice';
import LoginForm from '@/components/LoginForm';
import logo from '@/assets/image/logo.png';
import styles from './style.module.less';

const { Content } = Layout;

const Login: React.FC = () => {
  const isAuthenticated = useSelector(selectIsAuthenticated);

  // 如果已经登录，重定向到首页
  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  return (
    <Layout className={styles.layout}>
      <Content className={styles.content}>
        <div className={styles.container}>
          <div className={styles.logoContainer}>
            <img src={logo} alt="GIRA Logo" className={styles.logo} />
            <h1 className={styles.title}>GIRA</h1>
          </div>
          <LoginForm />
        </div>
      </Content>
    </Layout>
  );
};

export default Login; 