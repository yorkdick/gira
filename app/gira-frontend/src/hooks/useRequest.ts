import { useCallback } from 'react';
import axios, { AxiosRequestConfig } from 'axios';
import { message } from 'antd';

const instance = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

instance.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response) {
      const { status, data } = error.response;
      switch (status) {
        case 401:
          localStorage.removeItem('token');
          window.location.href = '/login';
          message.error('登录已过期，请重新登录');
          break;
        case 403:
          message.error('没有权限执行此操作');
          break;
        case 404:
          message.error('请求的资源不存在');
          break;
        case 500:
          message.error('服务器错误，请稍后重试');
          break;
        default:
          message.error(data.message || '请求失败');
      }
    } else if (error.request) {
      message.error('网络错误，请检查网络连接');
    } else {
      message.error('请求配置错误');
    }
    return Promise.reject(error);
  }
);

export const useRequest = () => {
  const get = useCallback(async <T>(url: string, config?: AxiosRequestConfig): Promise<T> => {
    return instance.get(url, config);
  }, []);

  const post = useCallback(async <T>(
    url: string,
    data?: Record<string, unknown>,
    config?: AxiosRequestConfig
  ): Promise<T> => {
    return instance.post(url, data, config);
  }, []);

  const put = useCallback(async <T>(
    url: string,
    data?: Record<string, unknown>,
    config?: AxiosRequestConfig
  ): Promise<T> => {
    return instance.put(url, data, config);
  }, []);

  const del = useCallback(async <T>(url: string, config?: AxiosRequestConfig): Promise<T> => {
    return instance.delete(url, config);
  }, []);

  return {
    get,
    post,
    put,
    delete: del,
  };
};

export default useRequest; 