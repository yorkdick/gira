import React from 'react';
import { Tag } from 'antd';
import {
  CheckCircleOutlined,
  ClockCircleOutlined,
  SyncOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons';
import { TaskStatus } from '@/types/task';
import styles from './style.module.less';

interface StatusTagProps {
  status: TaskStatus;
  showIcon?: boolean;
}

const statusConfig: Record<TaskStatus, {
  color: string;
  icon: React.ReactNode;
  text: string;
}> = {
  [TaskStatus.TODO]: {
    color: '#d9d9d9',
    icon: <ClockCircleOutlined />,
    text: '待处理',
  },
  [TaskStatus.IN_PROGRESS]: {
    color: '#1890ff',
    icon: <SyncOutlined spin />,
    text: '进行中',
  },
  [TaskStatus.IN_REVIEW]: {
    color: '#722ed1',
    icon: <ExclamationCircleOutlined />,
    text: '审核中',
  },
  [TaskStatus.DONE]: {
    color: '#52c41a',
    icon: <CheckCircleOutlined />,
    text: '已完成',
  },
};

const StatusTag: React.FC<StatusTagProps> = ({ status, showIcon = true }) => {
  const config = statusConfig[status];

  return (
    <Tag
      className={styles.statusTag}
      color={config.color}
      icon={showIcon ? config.icon : null}
    >
      {config.text}
    </Tag>
  );
};

export default StatusTag; 