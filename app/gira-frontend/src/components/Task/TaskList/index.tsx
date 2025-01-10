import React from 'react';
import { Table, Tag, Space, Button } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { Task, TaskStatus, TaskPriority } from '@/types/task';
import { usePermission } from '@/hooks/usePermission';

interface TaskListProps {
  tasks: Task[];
  onEdit?: (task: Task) => void;
  onDelete?: (taskId: number) => void;
}

const priorityColors = {
  [TaskPriority.LOW]: 'blue',
  [TaskPriority.MEDIUM]: 'orange',
  [TaskPriority.HIGH]: 'red',
};

const statusColors = {
  [TaskStatus.TODO]: 'default',
  [TaskStatus.IN_PROGRESS]: 'processing',
  [TaskStatus.DONE]: 'success',
};

const TaskList: React.FC<TaskListProps> = ({
  tasks,
  onEdit,
  onDelete,
}) => {
  const { isAdmin } = usePermission();

  const columns: ColumnsType<Task> = [
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: TaskStatus) => (
        <Tag color={statusColors[status]}>{status}</Tag>
      ),
      filters: Object.values(TaskStatus).map((status) => ({
        text: status,
        value: status,
      })),
      onFilter: (value, record) => record.status === value,
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      key: 'priority',
      render: (priority: TaskPriority) => (
        <Tag color={priorityColors[priority]}>{priority}</Tag>
      ),
      filters: Object.values(TaskPriority).map((priority) => ({
        text: priority,
        value: priority,
      })),
      onFilter: (value, record) => record.priority === value,
    },
    {
      title: '经办人',
      dataIndex: 'assignee',
      key: 'assignee',
      render: (assignee) => assignee?.username || '-',
    },
    {
      title: '报告人',
      dataIndex: 'reporter',
      key: 'reporter',
      render: (reporter) => reporter.username,
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date: string) => new Date(date).toLocaleDateString(),
      sorter: (a, b) =>
        new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime(),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space>
          {(isAdmin || record.assignee?.id === record.reporter.id) && (
            <>
              <Button
                type="text"
                icon={<EditOutlined />}
                onClick={() => onEdit?.(record)}
              />
              <Button
                type="text"
                danger
                icon={<DeleteOutlined />}
                onClick={() => onDelete?.(record.id)}
              />
            </>
          )}
        </Space>
      ),
    },
  ];

  return (
    <Table
      columns={columns}
      dataSource={tasks}
      rowKey="id"
      pagination={{
        defaultPageSize: 10,
        showSizeChanger: true,
        showQuickJumper: true,
      }}
    />
  );
};

export type { TaskListProps };
export default TaskList; 