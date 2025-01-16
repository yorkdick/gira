import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface Task {
  id: string;
  title: string;
  description: string;
  status: 'TODO' | 'IN_PROGRESS' | 'DONE';
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  assigneeId?: string;
  sprintId?: string;
  createdAt: string;
  updatedAt: string;
}

export interface TaskCreateDTO {
  title: string;
  description?: string;
  priority: Task['priority'];
  status: Task['status'];
  assigneeId?: string;
  dueDate?: string;
  tags?: string[];
}

export interface TaskUpdateDTO {
  title?: string;
  description?: string;
  priority?: Task['priority'];
  assigneeId?: string;
  dueDate?: string;
  tags?: string[];
}

export interface Board {
  id: string;
  name: string;
  description: string;
  status: 'active' | 'archived';
  createdAt: string;
  updatedAt: string;
}

interface BoardState {
  currentBoard: Board | null;
  boards: Board[];
  tasks: Task[];
  loading: boolean;
  error: string | null;
}

const initialState: BoardState = {
  currentBoard: null,
  boards: [],
  tasks: [],
  loading: false,
  error: null,
};

const boardSlice = createSlice({
  name: 'board',
  initialState,
  reducers: {
    setCurrentBoard: (state, action: PayloadAction<Board | null>) => {
      state.currentBoard = action.payload;
    },
    setBoards: (state, action: PayloadAction<Board[]>) => {
      state.boards = action.payload;
    },
    setTasks: (state, action: PayloadAction<Task[]>) => {
      state.tasks = action.payload;
    },
    updateTask: (state, action: PayloadAction<Task>) => {
      const index = state.tasks.findIndex(task => task.id === action.payload.id);
      if (index !== -1) {
        state.tasks[index] = action.payload;
      }
    },
    deleteTask: (state, action: PayloadAction<string>) => {
      state.tasks = state.tasks.filter(task => task.id !== action.payload);
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.loading = action.payload;
    },
    setError: (state, action: PayloadAction<string | null>) => {
      state.error = action.payload;
    },
  },
});

export const {
  setCurrentBoard,
  setBoards,
  setTasks,
  updateTask,
  deleteTask,
  setLoading,
  setError
} = boardSlice.actions;

export default boardSlice.reducer; 