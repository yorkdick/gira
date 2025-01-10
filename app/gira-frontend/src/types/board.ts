import { Task } from './task';
import { User } from './user';

export interface BoardSettings {
  wipLimit?: number;
  allowSubtasks?: boolean;
  requireEstimation?: boolean;
}

export interface BoardColumn {
  id: number;
  name: string;
  order: number;
  settings: BoardSettings;
}

export interface Board {
  id: number;
  name: string;
  description: string;
  columns: BoardColumn[];
  tasks: Task[];
  members: User[];
  createdAt: string;
  updatedAt: string;
  settings: {
    defaultColumnId?: number;
    defaultAssigneeId?: number;
  };
}

export interface CreateBoardParams {
  name: string;
  description: string;
  columns: Array<{
    name: string;
    settings?: BoardSettings;
  }>;
}

export interface UpdateBoardParams {
  name?: string;
  description?: string;
  settings?: {
    defaultColumnId?: number;
    defaultAssigneeId?: number;
  };
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