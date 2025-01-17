import React, { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { RootState } from '@/store/types';
import { logout, setUser } from '@/store/slices/authSlice';
import PrivateRoute from '@/components/PrivateRoute';
import BaseLayout from '@/layouts/BaseLayout';
import Login from '@/pages/Login';
import Board from '@/pages/Board';
import Sprints from '@/pages/Sprints';
import Users from '@/pages/Users';
import Settings from '@/pages/Settings';
import authService from '@/services/authService';

const App: React.FC = () => {
  const dispatch = useDispatch();
  const { user, token } = useSelector((state: RootState) => state.auth);

  useEffect(() => {
    const validateAuth = async () => {
      if (token && !user) {
        try {
          const response = await authService.getCurrentUser();
          dispatch(setUser(response.data));
        } catch (error) {
          console.error('Failed to validate token:', error);
          dispatch(logout());
        }
      }
    };
    void validateAuth();
  }, [dispatch, token, user]);

  return (
    <Router>
      <Routes>
        <Route path="/login" element={
          user ? <Navigate to="/board" replace /> : <Login />
        } />
        
        <Route path="/" element={
          <PrivateRoute>
            <BaseLayout />
          </PrivateRoute>
        }>
          <Route path="board" element={<Board />} />
          <Route path="sprints" element={<Sprints />} />
          <Route path="users" element={<Users />} />
          <Route path="settings" element={<Settings />} />
          <Route index element={<Navigate to="/board" replace />} />
          <Route path="*" element={<Navigate to="/board" replace />} />
        </Route>
      </Routes>
    </Router>
  );
};

export default App;
