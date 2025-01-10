import React, { useMemo } from 'react';
import { Card, Progress, Statistic, Row, Col } from 'antd';
import { CheckCircleOutlined, ClockCircleOutlined } from '@ant-design/icons';
import { Task, TaskStatus } from '@/types/task';
import styles from './style.module.less';

interface SprintProgressProps {
  tasks: Task[];
}

const SprintProgress: React.FC<SprintProgressProps> = ({ tasks }) => {
  const stats = useMemo(() => {
    const totalTasks = tasks.length;
    const completedTasks = tasks.filter(
      task => task.status === TaskStatus.DONE
    ).length;
    const completionRate = totalTasks ? (completedTasks / totalTasks) * 100 : 0;

    const totalHours = tasks.reduce((sum, task) => sum + (task.estimatedHours || 0), 0);
    const spentHours = tasks.reduce((sum, task) => sum + (task.spentHours || 0), 0);
    const remainingHours = Math.max(0, totalHours - spentHours);

    return {
      totalTasks,
      completedTasks,
      completionRate,
      totalHours,
      spentHours,
      remainingHours,
    };
  }, [tasks]);

  return (
    <Card className={styles.progress}>
      <Row gutter={[16, 16]}>
        <Col span={12}>
          <Progress
            type="circle"
            percent={Math.round(stats.completionRate)}
            format={percent => `${percent}%`}
          />
          <div className={styles.label}>任务完成率</div>
        </Col>
        <Col span={12}>
          <Row gutter={[8, 16]}>
            <Col span={24}>
              <Statistic
                title="任务进度"
                value={stats.completedTasks}
                suffix={`/ ${stats.totalTasks}`}
                prefix={<CheckCircleOutlined />}
              />
            </Col>
            <Col span={24}>
              <Statistic
                title="剩余工时"
                value={stats.remainingHours}
                suffix="小时"
                prefix={<ClockCircleOutlined />}
                valueStyle={
                  stats.remainingHours > stats.totalHours * 0.8
                    ? { color: '#f5222d' }
                    : undefined
                }
              />
            </Col>
          </Row>
        </Col>
      </Row>
    </Card>
  );
};

export default SprintProgress; 