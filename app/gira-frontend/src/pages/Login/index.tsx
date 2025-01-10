import React from 'react';
import { Layout } from 'antd';
import LoginForm from '@/components/LoginForm';
import styles from './style.module.less';

const { Content } = Layout;

const Login: React.FC = () => {
  return (
    <Layout className={styles.layout}>
      <Content className={styles.content}>
        <div className={styles.container}>
          <h1 className={styles.title}>GIRA</h1>
          <LoginForm />
        </div>
      </Content>
    </Layout>
  );
};

export default Login; 