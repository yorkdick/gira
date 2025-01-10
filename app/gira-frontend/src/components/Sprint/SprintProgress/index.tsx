import { Card, Progress, Space, Statistic, Tooltip } from 'antd';
import { CheckCircleOutlined, ClockCircleOutlined } from '@ant-design/icons';
import type { Task } from '@/types/task';
import { TaskStatus } from '@/types/task';

export interface SprintProgressProps {
  tasks: Task[];
}

function SprintProgress({ tasks }: SprintProgressProps): JSX.Element {
  // 计算Sprint进度
  const now = new Date();
  const start = new Date();  // TODO: Need startDate from parent
  const end = new Date();    // TODO: Need endDate from parent
  
  const totalDays = Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24));
  const passedDays = Math.ceil((now.getTime() - start.getTime()) / (1000 * 60 * 60 * 24));
  const timeProgress = Math.min(Math.max((passedDays / totalDays) * 100, 0), 100);

  // 计算任务完成进度
  const totalTasks = tasks.length;
  const completedTasks = tasks.filter((task: Task) => task.status === TaskStatus.DONE).length;
  const taskProgress = totalTasks > 0 ? (completedTasks / totalTasks) * 100 : 0;

  // 计算剩余天数
  const remainingDays = Math.max(Math.ceil((end.getTime() - now.getTime()) / (1000 * 60 * 60 * 24)), 0);

  return (
    <Card title="Sprint进度">
      <Space direction="vertical" style={{ width: '100%' }} size="large">
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <Tooltip title="已完成任务/总任务数">
            <Statistic
              title="任务完成"
              value={`${completedTasks}/${totalTasks}`}
              prefix={<CheckCircleOutlined />}
            />
          </Tooltip>
          <Tooltip title="剩余天数">
            <Statistic
              title="剩余时间"
              value={remainingDays}
              suffix="天"
              prefix={<ClockCircleOutlined />}
            />
          </Tooltip>
        </div>

        <div>
          <div style={{ marginBottom: 8 }}>时间进度</div>
          <Tooltip title={`${timeProgress.toFixed(1)}%`}>
            <Progress
              percent={timeProgress}
              status={timeProgress >= 100 ? 'exception' : 'active'}
              strokeColor={{
                '0%': '#108ee9',
                '100%': timeProgress >= 100 ? '#ff4d4f' : '#87d068',
              }}
            />
          </Tooltip>
        </div>

        <div>
          <div style={{ marginBottom: 8 }}>任务进度</div>
          <Tooltip title={`${taskProgress.toFixed(1)}%`}>
            <Progress
              percent={taskProgress}
              status={
                taskProgress === 100
                  ? 'success'
                  : timeProgress >= 100
                  ? 'exception'
                  : 'active'
              }
            />
          </Tooltip>
        </div>
      </Space>
    </Card>
  );
}

export default SprintProgress; 