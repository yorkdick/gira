import React from 'react';
import { Layout } from 'antd';
import UserMenu from '@/components/UserMenu';
import styles from './style.module.less';

const { Header: AntHeader } = Layout;

const Header: React.FC = () => {
  return (
    <AntHeader className={styles.header}>
      <div className={styles.logo}>
        <h1>GIRA</h1>
      </div>
      <div className={styles.right}>
        <UserMenu />
      </div>
    </AntHeader>
  );
};

export default Header; 