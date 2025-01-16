import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { RootState } from '@/store/types';
import PrivateRoute from '@/components/PrivateRoute';
import BaseLayout from '@/layouts/BaseLayout';
import Login from '@/pages/Login';
import Board from '@/pages/Board';
import Sprints from '@/pages/Sprints';
import Users from '@/pages/Users';
import Settings from '@/pages/Settings';

const App: React.FC = () => {
  const { user } = useSelector((state: RootState) => state.auth);

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
