import { lazy } from 'react';
import { RouteObject } from 'react-router-dom';
import { withSuspense } from '../utils/withSuspense';

// 懒加载页面组件
const Login = lazy(() => import('../pages/Login'));
const Board = lazy(() => import('../pages/Board'));
const Backlog = lazy(() => import('../pages/Backlog'));
const NotFound = lazy(() => import('../pages/NotFound'));
const MainLayout = lazy(() => import('../layouts/MainLayout'));

export const routes: RouteObject[] = [
  {
    path: '/login',
    element: withSuspense(Login),
  },
  {
    path: '/',
    element: withSuspense(MainLayout),
    children: [
      {
        path: 'board',
        element: withSuspense(Board),
      },
      {
        path: 'backlog',
        element: withSuspense(Backlog),
      },
      {
        path: '*',
        element: withSuspense(NotFound),
      },
    ],
  },
]; 