import request from '@/utils/request';
import type { Task, TaskCreateDTO, TaskUpdateDTO } from '@/store/slices/boardSlice';

const taskService = {
  getTasks: () => request.get<Task[]>('/api/tasks'),
  getTask: (id: string) => request.get<Task>(`/api/tasks/${id}`),
  createTask: (data: TaskCreateDTO) => request.post<Task>('/api/tasks', data),
  updateTask: (id: string, data: TaskUpdateDTO) => request.put<Task>(`/api/tasks/${id}`, data),
  updateTaskStatus: (id: string, status: Task['status']) =>
    request.put<Task>(`/api/tasks/${id}/status`, { status }),
  deleteTask: (id: string) => request.delete(`/api/tasks/${id}`),
};

export default taskService; 