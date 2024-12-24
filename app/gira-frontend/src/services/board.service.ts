import api from './api';

export interface Board {
  id: number;
  name: string;
  description: string;
  projectId: number;
  columns: Column[];
  archived: boolean;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  updatedBy: string;
}

export interface Column {
  id: number;
  name: string;
  description: string;
  boardId: number;
  position: number;
  tasks: Task[];
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  updatedBy: string;
}

export interface Task {
  id: number;
  title: string;
  description: string;
  columnId: number;
  position: number;
  priority: string;
  status: string;
  type: string;
  assigneeId: number;
  labelIds: number[];
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  updatedBy: string;
}

export interface CreateBoardRequest {
  name: string;
  description: string;
  projectId: number;
}

export interface UpdateBoardRequest {
  name?: string;
  description?: string;
}

class BoardService {
  async getBoards(projectId: number, page = 0, size = 10) {
    const response = await api.get<{ content: Board[]; totalElements: number }>(
      `/projects/${projectId}/boards?page=${page}&size=${size}`
    );
    return response.data;
  }

  async getBoardById(id: number) {
    const response = await api.get<Board>(`/boards/${id}`);
    return response.data;
  }

  async createBoard(data: CreateBoardRequest) {
    const response = await api.post<Board>('/boards', data);
    return response.data;
  }

  async updateBoard(id: number, data: UpdateBoardRequest) {
    const response = await api.put<Board>(`/boards/${id}`, data);
    return response.data;
  }

  async deleteBoard(id: number) {
    await api.delete(`/boards/${id}`);
  }

  async archiveBoard(id: number) {
    const response = await api.post<Board>(`/boards/${id}/archive`);
    return response.data;
  }

  async unarchiveBoard(id: number) {
    const response = await api.post<Board>(`/boards/${id}/unarchive`);
    return response.data;
  }

  async updateBoardColumnsOrder(id: number, columnIds: number[]) {
    const response = await api.put<Board>(`/boards/${id}/columns/order`, { columnIds });
    return response.data;
  }
}

export default new BoardService(); 