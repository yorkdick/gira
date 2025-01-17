/**
 * Redux Store 类型定义文件
 * 
 * 包含了整个应用的状态类型定义，包括：
 * - 认证状态 (auth)
 * - 看板状态 (board)
 * - Sprint状态 (sprint)
 */

import { store } from './index';
import type { UserInfo } from './slices/authSlice';
import type { Board, Task } from './slices/boardSlice';
import type { Sprint } from './slices/sprintSlice';
import type { UserState } from './slices/userSlice';

/**
 * 根状态接口
 * 定义了整个Redux store的状态结构
 * @interface
 */
export interface RootState {
  /** 认证相关状态 */
  auth: {
    /** 访问令牌 */
    token: string | null;
    /** 当前登录用户信息 */
    user: UserInfo | null;
    /** 加载状态 */
    loading: boolean;
    /** 错误信息 */
    error: string | null;
  };
  /** 看板相关状态 */
  board: {
    /** 看板列表 */
    boards: Board[];
    /** 当前选中的看板 */
    currentBoard: Board | null;
    /** 任务列表 */
    tasks: Task[];
    /** 加载状态 */
    loading: boolean;
    /** 错误信息 */
    error: string | null;
  };
  /** Sprint相关状态 */
  sprint: {
    /** Sprint列表 */
    sprints: Sprint[];
    /** 当前选中的Sprint */
    currentSprint: Sprint | null;
    /** 加载状态 */
    loading: boolean;
    /** 错误信息 */
    error: string | null;
  };
  /** 用户管理相关状态 */
  user: {
    /** 用户列表 */
    list: UserInfo[];
    /** 加载状态 */
    loading: boolean;
    /** 错误信息 */
    error: string | null;
  };
  users: UserState;
}

/**
 * Redux dispatch 函数类型
 * 用于类型安全的 dispatch 操作
 */
export type AppDispatch = typeof store.dispatch; 