import type { Task } from './task';

export interface BoardSettings {
  wipLimit?: number;
  allowSubtasks?: boolean;
  requireEstimation?: boolean;
}

export interface BoardColumn {
  id: number;
  name: string;
  wipLimit: number;
  order?: number;
  settings: BoardSettings;
}

export interface Board {
  id: number;
  name: string;
  description?: string;
  columns: BoardColumn[];
  tasks: Task[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateBoardParams {
  name: string;
  description?: string;
  columns: Omit<BoardColumn, 'id'>[];
}

export interface UpdateBoardParams {
  name?: string;
  description?: string;
  columns?: BoardColumn[];
}

export interface UpdateColumnOrderParams {
  columnId: number;
  order: number;
}

export interface UpdateTaskOrderParams {
  taskId: number;
  columnId: number;
  order: number;
}

export interface BoardListResult {
  items: Board[];
  total: number;
  page: number;
  pageSize: number;
}

export interface BoardQueryParams {
  page?: number;
  pageSize?: number;
  keyword?: string;
} 