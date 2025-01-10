import { lazy } from 'react';
import { RouteObject } from 'react-router-dom';
import { withSuspense } from '../utils/withSuspense';
import PrivateRoute from '@/components/PrivateRoute';

const Login = lazy(() => import('../pages/Login'));
const Board = lazy(() => import('../pages/Board'));
const Backlog = lazy(() => import('../pages/Backlog'));
const NotFound = lazy(() => import('../pages/NotFound'));
const MainLayout = lazy(() => import('../layouts/MainLayout'));

const PrivateBoard = () => (
  <PrivateRoute>
    <Board />
  </PrivateRoute>
);

const PrivateBacklog = () => (
  <PrivateRoute>
    <Backlog />
  </PrivateRoute>
);

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
        path: '',
        element: withSuspense(PrivateBoard),
      },
      {
        path: 'board',
        element: withSuspense(PrivateBoard),
      },
      {
        path: 'backlog',
        element: withSuspense(PrivateBacklog),
      },
      {
        path: '*',
        element: withSuspense(NotFound),
      },
    ],
  },
]; 