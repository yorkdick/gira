import { User } from './user';

// 任务优先级
export enum TaskPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH'
}

// 任务状态
export enum TaskStatus {
  TODO = 'TODO',
  IN_PROGRESS = 'IN_PROGRESS',
  DONE = 'DONE'
}

// 任务类型
export interface Task {
  id: number;
  title: string;
  description?: string;
  status: TaskStatus;
  priority: TaskPriority;
  assignee?: User;
  reporter: User;
  sprintId?: number;
  columnId: number;
  order: number;
  labels?: string[];
  dueDate?: string;
  createdAt: string;
  updatedAt: string;
}

// 任务列表查询参数
export interface TaskQueryParams {
  projectId?: number;
  sprintId?: number;
  columnId?: number;
  assigneeId?: number;
  reporterId?: number;
  status?: TaskStatus;
  priority?: TaskPriority;
  keyword?: string;
  page?: number;
  pageSize?: number;
  boardId?: number;
}

// 任务列表响应数据
export interface TaskListResult {
  items: Task[];
  total: number;
  page: number;
  pageSize: number;
}

// 创建任务参数
export interface CreateTaskParams {
  title: string;
  description: string;
  priority: TaskPriority;
  status: TaskStatus;
  projectId: number;
  columnId: number;
  assigneeId?: number;
  sprintId?: number;
  labels?: string[];
  dueDate?: string;
}

// 更新任务参数
export interface UpdateTaskParams {
  title?: string;
  description?: string;
  status?: TaskStatus;
  priority?: TaskPriority;
  assigneeId?: number;
  sprintId?: number;
  columnId?: number;
  order?: number;
} 