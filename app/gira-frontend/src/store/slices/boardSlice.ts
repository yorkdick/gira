import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { message } from 'antd';
import {
  Board,
  BoardQueryParams,
  CreateBoardParams,
  UpdateBoardParams,
  UpdateColumnOrderParams,
  UpdateTaskOrderParams,
} from '@/types/board';
import * as boardService from '@/services/board';
import {
  NormalizedEntity,
  normalizeEntities,
  addEntity,
  updateEntity,
  createEntitySelectors,
} from '@/utils/normalize';
import { RootState } from '@/store';

interface BoardState {
  entities: NormalizedEntity<Board>;
  currentBoardId: number | null;
  loading: boolean;
  error: string | null;
  total: number;
  page: number;
  pageSize: number;
  cache: {
    [key: string]: {
      data: number[];
      timestamp: number;
    };
  };
  currentBoard: Board | null;
}

const initialState: BoardState = {
  entities: {
    byId: {},
    allIds: [],
  },
  currentBoardId: null,
  loading: false,
  error: null,
  total: 0,
  page: 1,
  pageSize: 10,
  cache: {},
  currentBoard: null,
};

// 缓存时间：5分钟
const CACHE_DURATION = 5 * 60 * 1000;

// 生成缓存key
const generateCacheKey = (params?: BoardQueryParams): string => {
  if (!params) return 'all';
  return JSON.stringify(params);
};

// 检查缓存是否有效
const isCacheValid = (timestamp: number): boolean => {
  return Date.now() - timestamp < CACHE_DURATION;
};

export const fetchBoards = createAsyncThunk<
  { items: Board[]; total: number; page: number; pageSize: number },
  BoardQueryParams | undefined,
  { state: RootState }
>('board/fetchBoards', async (params, { getState }) => {
  const state = getState();
  const cacheKey = generateCacheKey(params);
  const cache = state.board.cache[cacheKey];

  // 如果缓存有效，直接返回缓存数据
  if (cache && isCacheValid(cache.timestamp)) {
    return {
      items: cache.data.map((id) => state.board.entities.byId[id]),
      total: cache.data.length,
      page: state.board.page,
      pageSize: state.board.pageSize,
    };
  }

  const response = await boardService.getBoards(params || { projectId: 0 });
  return response;
});

export const fetchBoard = createAsyncThunk(
  'board/fetchBoard',
  async (id: number) => {
    const response = await boardService.getBoard(id);
    return response;
  }
);

export const createBoard = createAsyncThunk(
  'board/createBoard',
  async (params: CreateBoardParams) => {
    const response = await boardService.createBoard(params);
    message.success('看板创建成功');
    return response;
  }
);

export const updateBoard = createAsyncThunk(
  'board/updateBoard',
  async ({ id, params }: { id: number; params: UpdateBoardParams }) => {
    const response = await boardService.updateBoard(id, params);
    message.success('看板更新成功');
    return response;
  }
);

export const updateColumnOrder = createAsyncThunk(
  'board/updateColumnOrder',
  async ({ boardId, params }: { boardId: number; params: UpdateColumnOrderParams }) => {
    await boardService.updateColumnOrder(boardId, params);
    return { boardId, ...params };
  }
);

export const updateTaskOrder = createAsyncThunk(
  'board/updateTaskOrder',
  async ({ boardId, params }: { boardId: number; params: UpdateTaskOrderParams }) => {
    await boardService.updateTaskOrder(boardId, params);
    return { boardId, ...params };
  }
);

const boardSlice = createSlice({
  name: 'board',
  initialState,
  reducers: {
    clearBoards: (state) => {
      state.entities = {
        byId: {},
        allIds: [],
      };
      state.total = 0;
      state.cache = {};
    },
    clearCurrentBoard: (state) => {
      state.currentBoardId = null;
    },
    clearCache: (state) => {
      state.cache = {};
    },
  },
  extraReducers: (builder) => {
    builder
      // fetchBoards
      .addCase(fetchBoards.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchBoards.fulfilled, (state, action) => {
        state.loading = false;
        const normalized = normalizeEntities(action.payload.items);
        state.entities = normalized;
        state.total = action.payload.total;
        state.page = action.payload.page;
        state.pageSize = action.payload.pageSize;

        // 更新缓存
        const cacheKey = generateCacheKey(action.meta.arg);
        state.cache[cacheKey] = {
          data: normalized.allIds,
          timestamp: Date.now(),
        };
      })
      .addCase(fetchBoards.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || '获取看板列表失败';
        message.error(state.error);
      })
      // fetchBoard
      .addCase(fetchBoard.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchBoard.fulfilled, (state, action) => {
        state.loading = false;
        state.entities = addEntity(state.entities, action.payload);
        state.currentBoardId = action.payload.id;
      })
      .addCase(fetchBoard.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || '获取看板详情失败';
        message.error(state.error);
      })
      // createBoard
      .addCase(createBoard.fulfilled, (state, action) => {
        state.entities = addEntity(state.entities, action.payload);
        state.total += 1;
        state.cache = {}; // 清除缓存
      })
      // updateBoard
      .addCase(updateBoard.fulfilled, (state, action) => {
        state.entities = updateEntity(state.entities, action.payload);
        state.cache = {}; // 清除缓存
      })
      // updateColumnOrder
      .addCase(updateColumnOrder.fulfilled, (state, action) => {
        const board = state.entities.byId[action.payload.boardId];
        if (board) {
          const updatedBoard = {
            ...board,
            columns: board.columns.map((col) =>
              col.id === action.payload.columnId
                ? { ...col, order: action.payload.order }
                : col
            ),
          };
          state.entities = updateEntity(state.entities, updatedBoard);
        }
        state.cache = {}; // 清除缓存
      })
      // updateTaskOrder
      .addCase(updateTaskOrder.fulfilled, (state, action) => {
        const board = state.entities.byId[action.payload.boardId];
        if (board) {
          const updatedBoard = {
            ...board,
            columns: board.columns.map((col) =>
              col.id === action.payload.columnId
                ? {
                    ...col,
                    taskIds: col.taskIds.filter((id) => id !== action.payload.taskId),
                  }
                : col
            ),
          };
          state.entities = updateEntity(state.entities, updatedBoard);
        }
        state.cache = {}; // 清除缓存
      });
  },
});

export const { clearBoards, clearCurrentBoard, clearCache } = boardSlice.actions;

// 创建选择器
const selectBoardState = (state: RootState) => state.board.entities;

export const {
  selectIds: selectBoardIds,
  selectById: selectBoardById,
  selectAll: selectAllBoards,
  selectTotal: selectTotalBoards,
} = createEntitySelectors<Board, RootState>(selectBoardState);

// 其他选择器
export const selectCurrentBoard = (state: RootState) =>
  state.board.currentBoardId ? state.board.entities.byId[state.board.currentBoardId] : null;
export const selectBoardLoading = (state: RootState) => state.board.loading;
export const selectBoardError = (state: RootState) => state.board.error;
export const selectBoardTotal = (state: RootState) => state.board.total;

export default boardSlice.reducer; 