import React from 'react';
import { Input, Select, Space, Button } from 'antd';
import { FilterOutlined } from '@ant-design/icons';
import { TaskStatus, TaskPriority } from '@/types/task';
import { User } from '@/types/user';
import styles from './style.module.less';

const { Search } = Input;

interface BoardFilterProps {
  users: User[];
  onSearch: (keyword: string) => void;
  onFilter: (filters: BoardFilterValues) => void;
}

export interface BoardFilterValues {
  status?: TaskStatus;
  priority?: TaskPriority;
  assigneeId?: number;
  keyword?: string;
}

const BoardFilter: React.FC<BoardFilterProps> = ({
  users,
  onSearch,
  onFilter,
}) => {
  const [filters, setFilters] = React.useState<BoardFilterValues>({});

  const handleFilterChange = (
    key: keyof BoardFilterValues,
    value: BoardFilterValues[keyof BoardFilterValues]
  ) => {
    const newFilters = { ...filters, [key]: value };
    setFilters(newFilters);
    onFilter(newFilters);
  };

  const handleClearFilters = () => {
    setFilters({});
    onFilter({});
  };

  return (
    <div className={styles.filter}>
      <Space size="middle">
        <Search
          placeholder="搜索任务"
          allowClear
          onSearch={onSearch}
          style={{ width: 200 }}
        />
        <Select
          placeholder="状态"
          allowClear
          style={{ width: 120 }}
          value={filters.status}
          onChange={(value) => handleFilterChange('status', value)}
        >
          <Select.Option value={TaskStatus.TODO}>待处理</Select.Option>
          <Select.Option value={TaskStatus.IN_PROGRESS}>进行中</Select.Option>
          <Select.Option value={TaskStatus.IN_REVIEW}>审核中</Select.Option>
          <Select.Option value={TaskStatus.DONE}>已完成</Select.Option>
        </Select>
        <Select
          placeholder="优先级"
          allowClear
          style={{ width: 120 }}
          value={filters.priority}
          onChange={(value) => handleFilterChange('priority', value)}
        >
          <Select.Option value={TaskPriority.LOW}>低</Select.Option>
          <Select.Option value={TaskPriority.MEDIUM}>中</Select.Option>
          <Select.Option value={TaskPriority.HIGH}>高</Select.Option>
          <Select.Option value={TaskPriority.URGENT}>紧急</Select.Option>
        </Select>
        <Select
          placeholder="经办人"
          allowClear
          style={{ width: 120 }}
          value={filters.assigneeId}
          onChange={(value) => handleFilterChange('assigneeId', value)}
        >
          {users.map((user) => (
            <Select.Option key={user.id} value={user.id}>
              {user.name}
            </Select.Option>
          ))}
        </Select>
        <Button
          icon={<FilterOutlined />}
          onClick={handleClearFilters}
          disabled={Object.keys(filters).length === 0}
        >
          清除筛选
        </Button>
      </Space>
    </div>
  );
};

export default BoardFilter; 