import React from 'react';
import { Button, Space } from 'antd';
import { PlusOutlined, SearchOutlined } from '@ant-design/icons';
import styles from './style.module.less';

const QuickActions: React.FC = () => {
  const handleCreateProject = () => {
    // TODO: 实现创建项目功能
    console.log('创建项目');
  };

  const handleCreateTask = () => {
    // TODO: 实现创建任务功能
    console.log('创建任务');
  };

  const handleSearch = () => {
    // TODO: 实现搜索功能
    console.log('搜索');
  };

  return (
    <div className={styles.container}>
      <Space direction="vertical" style={{ width: '100%' }}>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          block
          onClick={handleCreateProject}
        >
          创建项目
        </Button>
        <Button
          icon={<PlusOutlined />}
          block
          onClick={handleCreateTask}
        >
          创建任务
        </Button>
        <Button
          icon={<SearchOutlined />}
          block
          onClick={handleSearch}
        >
          搜索
        </Button>
      </Space>
    </div>
  );
};

export default QuickActions; 