import { lazy } from 'react';
import { Navigate } from 'react-router-dom';
import { RouteObject } from 'react-router-dom';
import BaseLayout from '../layouts/BaseLayout';

// 懒加载页面组件
const Login = lazy(() => import('../pages/Login'));
const Board = lazy(() => import('../pages/Board'));
const Sprints = lazy(() => import('../pages/Sprints'));
const Users = lazy(() => import('../pages/Users'));
const Settings = lazy(() => import('../pages/Settings'));

export const routes: RouteObject[] = [
  {
    path: '/login',
    element: <Login />,
  },
  {
    path: '/',
    element: <BaseLayout />,
    children: [
      {
        path: '',
        element: <Navigate to="/board" replace />,
      },
      {
        path: 'board',
        element: <Board />,
      },
      {
        path: 'sprints',
        element: <Sprints />,
      },
      {
        path: 'users',
        element: <Users />,
      },
      {
        path: 'settings',
        element: <Settings />,
      },
    ],
  },
];

export default routes; 