import request from '@/utils/request';

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
  getBoards: () => request.get<Board[]>('/api/boards'),
  getActiveBoard: () => request.get<Board>('/api/boards/active'),
  updateBoard: (id: string, data: BoardUpdateDTO) =>
    request.put<Board>(`/api/boards/${id}`, data),
  archiveBoard: (id: string) =>
    request.put<Board>(`/api/boards/${id}/archive`),
  activateBoard: (id: string) =>
    request.put<Board>(`/api/boards/${id}/activate`),
  getBoardStats: (id: string) =>
    request.get<{
      totalTasks: number;
      completedTasks: number;
      inProgressTasks: number;
      todoTasks: number;
    }>(`/api/boards/${id}/stats`),
};

export default boardService; 