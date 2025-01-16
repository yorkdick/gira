import request from '@/utils/request';
import type { Task } from '@/store/slices/boardSlice';

export interface Board {
  id: string;
  name: string;
  description: string;
  status: 'ACTIVE' | 'ARCHIVED';
  createdBy: {
    id: string;
    name: string;
  };
  createdAt: string;
}

export interface BoardUpdateDTO {
  name?: string;
  description?: string;
  status?: Board['status'];
}

const boardService = {
  getBoards: () => request.get<Board[]>('/boards'),
  getActiveBoard: () => request.get<Board>('/boards/active'),
  updateBoard: (id: string, data: BoardUpdateDTO) =>
    request.put<Board>(`/boards/${id}`, data),
  archiveBoard: (id: string) =>
    request.put<Board>(`/boards/${id}/archive`),
  activateBoard: (id: string) =>
    request.put<Board>(`/boards/${id}/activate`),
  getBoardStats: (id: string) =>
    request.get<{
      totalTasks: number;
      completedTasks: number;
      inProgressTasks: number;
      todoTasks: number;
    }>(`/boards/${id}/stats`),

  getTasks: async () => {
    const { data: activeBoard } = await request.get<Board>('/boards/active');
    return request.get<Task[]>(`/boards/${activeBoard.id}/tasks`);
  },
  getTask: (id: string) => request.get<Task>(`/tasks/${id}`),
  createTask: (data: Omit<Task, 'id' | 'createdAt' | 'updatedAt'>) =>
    request.post<Task>('/tasks', data),
  updateTask: (id: string, data: Partial<Task>) =>
    request.put<Task>(`/tasks/${id}`, data),
  deleteTask: (id: string) =>
    request.delete(`/tasks/${id}`),
  updateTaskStatus: (id: string, status: Task['status']) =>
    request.put<Task>(`/tasks/${id}/status`, { status }),
};

export default boardService; 