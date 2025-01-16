// API响应的基础类型
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

// 分页响应的基础类型
export interface PaginatedResponse<T> {
  code: number;
  message: string;
  data: {
    items: T[];
    total: number;
    page: number;
    pageSize: number;
  };
}

// 错误响应的类型
export interface ApiError {
  code: number;
  message: string;
  details?: Record<string, string[]>;
} 