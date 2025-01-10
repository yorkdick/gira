// 看板列类型
export interface BoardColumn {
  id: number;
  name: string;
  order: number;
  taskIds: number[];
}

// 看板类型
export interface Board {
  id: number;
  name: string;
  projectId: number;
  columns: BoardColumn[];
  createdAt: string;
  updatedAt: string;
}

// 看板列表查询参数
export interface BoardQueryParams {
  projectId: number;
  page?: number;
  pageSize?: number;
}

// 看板列表响应数据
export interface BoardListResult {
  items: Board[];
  total: number;
  page: number;
  pageSize: number;
}

// 创建看板参数
export interface CreateBoardParams {
  name: string;
  projectId: number;
  columns: Omit<BoardColumn, 'id' | 'taskIds'>[];
}

// 更新看板参数
export interface UpdateBoardParams {
  name?: string;
  columns?: Omit<BoardColumn, 'taskIds'>[];
}

// 更新看板列顺序参数
export interface UpdateColumnOrderParams {
  columnId: number;
  order: number;
}

// 更新任务顺序参数
export interface UpdateTaskOrderParams {
  taskId: number;
  columnId: number;
  order: number;
} 