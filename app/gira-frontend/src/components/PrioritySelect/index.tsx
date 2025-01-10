import React from 'react';
import { Select, Tag } from 'antd';
import {
  FlagOutlined,
  WarningOutlined,
  ExclamationCircleOutlined,
  ThunderboltOutlined,
} from '@ant-design/icons';
import { TaskPriority } from '@/types/task';
import styles from './style.module.less';

const { Option } = Select;

interface PrioritySelectProps {
  value?: TaskPriority;
  onChange?: (value: TaskPriority) => void;
  disabled?: boolean;
}

const priorityConfig: Record<TaskPriority, {
  color: string;
  icon: React.ReactNode;
  text: string;
}> = {
  [TaskPriority.LOW]: {
    color: '#52c41a',
    icon: <FlagOutlined />,
    text: '低优先级',
  },
  [TaskPriority.MEDIUM]: {
    color: '#faad14',
    icon: <WarningOutlined />,
    text: '中优先级',
  },
  [TaskPriority.HIGH]: {
    color: '#f5222d',
    icon: <ExclamationCircleOutlined />,
    text: '高优先级',
  },
  [TaskPriority.URGENT]: {
    color: '#ff4d4f',
    icon: <ThunderboltOutlined />,
    text: '紧急',
  },
};

const PrioritySelect: React.FC<PrioritySelectProps> = ({
  value,
  onChange,
  disabled,
}) => {
  const renderOption = (priority: TaskPriority) => {
    const config = priorityConfig[priority];
    return (
      <Option key={priority} value={priority}>
        <div className={styles.option}>
          <Tag color={config.color} icon={config.icon}>
            {config.text}
          </Tag>
        </div>
      </Option>
    );
  };

  return (
    <Select
      className={styles.prioritySelect}
      value={value}
      onChange={onChange}
      disabled={disabled}
      placeholder="选择优先级"
    >
      {Object.values(TaskPriority).map(renderOption)}
    </Select>
  );
};

export default PrioritySelect; 