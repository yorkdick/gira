import { UserInfo } from './slices/authSlice';
import { Board, Task } from './slices/boardSlice';
import { Sprint } from './slices/sprintSlice';
import { PersistPartial } from 'redux-persist/es/persistReducer';

export interface RootState {
  auth: {
    token: string | null;
    user: UserInfo | null;
    loading: boolean;
    error: string | null;
  } & PersistPartial;
  board: {
    currentBoard: Board | null;
    boards: Board[];
    tasks: Task[];
    loading: boolean;
    error: string | null;
  };
  sprint: {
    sprints: Sprint[];
    currentSprint: Sprint | null;
    loading: boolean;
    error: string | null;
  };
} 