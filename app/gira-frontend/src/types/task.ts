import { User } from './user';

// 任务优先级
export enum TaskPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  URGENT = 'URGENT'
}

// 任务状态
export enum TaskStatus {
  TODO = 'TODO',
  IN_PROGRESS = 'IN_PROGRESS',
  IN_REVIEW = 'IN_REVIEW',
  DONE = 'DONE'
}

// 任务类型
export interface Task {
  id: number;
  title: string;
  description: string;
  priority: TaskPriority;
  status: TaskStatus;
  assigneeId?: number;
  assignee?: User;
  reporterId: number;
  reporter?: User;
  projectId: number;
  sprintId?: number;
  columnId: number;
  order: number;
  dueDate?: string;
  estimatedHours?: number;
  spentHours?: number;
  labels: string[];
  attachments: string[];
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
  assigneeId?: number;
  projectId: number;
  sprintId?: number;
  columnId: number;
  dueDate?: string;
  estimatedHours?: number;
  labels?: string[];
}

// 更新任务参数
export interface UpdateTaskParams {
  title?: string;
  description?: string;
  priority?: TaskPriority;
  status?: TaskStatus;
  assigneeId?: number;
  sprintId?: number;
  columnId?: number;
  order?: number;
  dueDate?: string;
  estimatedHours?: number;
  spentHours?: number;
  labels?: string[];
}

// 任务评论
export interface TaskComment {
  id: number;
  taskId: number;
  content: string;
  userId: number;
  user?: User;
  createdAt: string;
  updatedAt: string;
}

// 创建任务评论参数
export interface CreateTaskCommentParams {
  taskId: number;
  content: string;
}

// 更新任务评论参数
export interface UpdateTaskCommentParams {
  content: string;
} 