/**
 * BoardSummary 组件 - 看板统计信息展示
 * 
 * 功能：
 * 1. 展示当前看板的任务统计信息
 * 2. 显示各状态任务的数量
 * 3. 显示任务完成进度
 * 
 * @component
 */

import React from 'react';
import { Card, Row, Col, Statistic } from 'antd';
import { useSelector } from 'react-redux';
import { RootState } from '@/store/types';
import styles from './index.module.less';

const BoardSummary: React.FC = () => {
  const { tasks } = useSelector((state: RootState) => state.board);

  const stats = {
    total: tasks.length,
    todo: tasks.filter(task => task.status === 'TODO').length,
    inProgress: tasks.filter(task => task.status === 'IN_PROGRESS').length,
    done: tasks.filter(task => task.status === 'DONE').length,
  };

  const completionRate = stats.total > 0
    ? Math.round((stats.done / stats.total) * 100)
    : 0;

  return (
    <Card className={styles.summary}>
      <Row gutter={16}>
        <Col span={6}>
          <Statistic title="总任务" value={stats.total} />
        </Col>
        <Col span={6}>
          <Statistic title="待办" value={stats.todo} />
        </Col>
        <Col span={6}>
          <Statistic title="进行中" value={stats.inProgress} />
        </Col>
        <Col span={6}>
          <Statistic title="已完成" value={stats.done} suffix={`${completionRate}%`} />
        </Col>
      </Row>
    </Card>
  );
};

export default BoardSummary; 