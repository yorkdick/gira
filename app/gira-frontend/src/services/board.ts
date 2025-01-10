import request from '@/utils/request';
import {
  Board,
  BoardListResult,
  BoardQueryParams,
  CreateBoardParams,
  UpdateBoardParams,
  UpdateColumnOrderParams,
  UpdateTaskOrderParams,
} from '@/types/board';

// 获取看板列表
export const getBoards = (params: BoardQueryParams): Promise<BoardListResult> => {
  return request.get<BoardListResult>('/boards', { params }).then(res => res.data);
};

// 获取看板详情
export const getBoard = (id: number): Promise<Board> => {
  return request.get<Board>(`/boards/${id}`).then(res => res.data);
};

// 创建看板
export const createBoard = (params: CreateBoardParams): Promise<Board> => {
  return request.post<Board>('/boards', params).then(res => res.data);
};

// 更新看板
export const updateBoard = (id: number, params: UpdateBoardParams): Promise<Board> => {
  return request.put<Board>(`/boards/${id}`, params).then(res => res.data);
};

// 删除看板
export const deleteBoard = (id: number): Promise<void> => {
  return request.delete<void>(`/boards/${id}`).then(res => res.data);
};

// 更新看板列顺序
export const updateColumnOrder = (boardId: number, params: UpdateColumnOrderParams): Promise<void> => {
  return request.put<void>(`/boards/${boardId}/columns/${params.columnId}/order`, { order: params.order }).then(res => res.data);
};

// 更新任务顺序
export const updateTaskOrder = (boardId: number, params: UpdateTaskOrderParams): Promise<void> => {
  return request.put<void>(`/boards/${boardId}/tasks/${params.taskId}/order`, {
    columnId: params.columnId,
    order: params.order,
  }).then(res => res.data);
}; 