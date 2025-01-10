import React from 'react';
import { Space, Select, Input, Button } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '@/store';
import { TaskPriority, TaskStatus } from '@/types/task';
import { fetchTasks } from '@/store/slices/taskSlice';
import styles from './style.module.less';

const { Option } = Select;

const BoardFilter: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { currentBoard } = useSelector((state: RootState) => state.board);

  const handleSearch = (value: string) => {
    if (currentBoard) {
      dispatch(
        fetchTasks({
          boardId: currentBoard.id,
          keyword: value || undefined,
        })
      );
    }
  };

  const handlePriorityChange = (value: TaskPriority | undefined) => {
    if (currentBoard) {
      dispatch(
        fetchTasks({
          boardId: currentBoard.id,
          priority: value,
        })
      );
    }
  };

  const handleStatusChange = (value: TaskStatus | undefined) => {
    if (currentBoard) {
      dispatch(
        fetchTasks({
          boardId: currentBoard.id,
          status: value,
        })
      );
    }
  };

  return (
    <Space size="middle" className={styles.container}>
      <Input.Search
        placeholder="搜索任务"
        allowClear
        onSearch={handleSearch}
        style={{ width: 200 }}
      />
      <Select
        placeholder="优先级"
        allowClear
        style={{ width: 120 }}
        onChange={handlePriorityChange}
      >
        {Object.values(TaskPriority).map((priority) => (
          <Option key={priority} value={priority}>
            {priority}
          </Option>
        ))}
      </Select>
      <Select
        placeholder="状态"
        allowClear
        style={{ width: 120 }}
        onChange={handleStatusChange}
      >
        {Object.values(TaskStatus).map((status) => (
          <Option key={status} value={status}>
            {status}
          </Option>
        ))}
      </Select>
      <Button
        type="primary"
        icon={<PlusOutlined />}
        className={styles.createButton}
      >
        创建任务
      </Button>
    </Space>
  );
};

export default BoardFilter; 