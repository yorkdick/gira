import request from '@/utils/request';
import type { Task } from '@/store/slices/boardSlice';

export interface CreateTaskRequest {
  title: string;
  description?: string;
  priority?: 'LOW' | 'MEDIUM' | 'HIGH';
  status?: 'TODO' | 'IN_PROGRESS' | 'DONE';
  sprintId?: string;
  assigneeId?: string;
}

const taskService = {
  // 任务 CRUD 接口
  getTasks: () => request.get<Task[]>('/tasks'),
  getTask: (id: string) => request.get<Task>(`/tasks/${id}`),
  createTask: (data: CreateTaskRequest) => request.post<Task>('/tasks', data),
  updateTask: (id: string, data: Partial<Task>) => request.put<Task>(`/tasks/${id}`, data),
  deleteTask: (id: string) => request.delete(`/tasks/${id}`),

  // 任务状态管理
  updateTaskStatus: (id: string, status: Task['status']) =>
    request.put<Task>(`/tasks/${id}/status`, { status }),

  // 任务分配
  getTasksByAssignee: (assigneeId: string) => request.get<Task[]>(`/tasks/assignee/${assigneeId}`),

  // Sprint 相关
  moveTaskToSprint: (taskId: string, sprintId: string) =>
    request.put<Task>(`/tasks/${taskId}/sprint/${sprintId}`),
};

export default taskService; 