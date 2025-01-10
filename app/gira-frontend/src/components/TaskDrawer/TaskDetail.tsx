import React from 'react';
import { Descriptions, Space } from 'antd';
import { Task } from '@/types/task';
import StatusTag from '@/components/StatusTag';
import { formatDateTime } from '@/utils/date';
import styles from './style.module.less';

interface TaskDetailProps {
  task: Task;
}

const TaskDetail: React.FC<TaskDetailProps> = ({ task }) => {
  return (
    <div className={styles.taskDetail}>
      <Descriptions column={2} bordered size="small">
        <Descriptions.Item label="状态" span={1}>
          <StatusTag status={task.status} />
        </Descriptions.Item>

        <Descriptions.Item label="优先级" span={1}>
          <Space>
            {task.priority}
          </Space>
        </Descriptions.Item>

        <Descriptions.Item label="负责人" span={1}>
          {task.assignee?.username || '-'}
        </Descriptions.Item>

        <Descriptions.Item label="报告人" span={1}>
          {task.reporter?.username || '-'}
        </Descriptions.Item>

        <Descriptions.Item label="创建时间" span={1}>
          {formatDateTime(task.createdAt)}
        </Descriptions.Item>

        <Descriptions.Item label="更新时间" span={1}>
          {formatDateTime(task.updatedAt)}
        </Descriptions.Item>

        <Descriptions.Item label="截止日期" span={1}>
          {task.dueDate ? formatDateTime(task.dueDate) : '-'}
        </Descriptions.Item>

        <Descriptions.Item label="预估工时" span={1}>
          {task.estimatedHours ? `${task.estimatedHours}小时` : '-'}
        </Descriptions.Item>

        <Descriptions.Item label="已用工时" span={1}>
          {task.spentHours ? `${task.spentHours}小时` : '-'}
        </Descriptions.Item>

        <Descriptions.Item label="标签" span={2}>
          {task.labels.length > 0 ? task.labels.join(', ') : '-'}
        </Descriptions.Item>

        <Descriptions.Item label="描述" span={2}>
          {task.description || '-'}
        </Descriptions.Item>

        <Descriptions.Item label="附件" span={2}>
          {task.attachments.length > 0 ? (
            <ul className={styles.attachments}>
              {task.attachments.map((attachment, index) => (
                <li key={index}>
                  <a href={attachment} target="_blank" rel="noopener noreferrer">
                    {attachment.split('/').pop()}
                  </a>
                </li>
              ))}
            </ul>
          ) : (
            '-'
          )}
        </Descriptions.Item>
      </Descriptions>
    </div>
  );
};

export default TaskDetail; 