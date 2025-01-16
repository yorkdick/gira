import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { RootState } from '@/store/types';

interface PrivateRouteProps {
  children: React.ReactNode;
}

const PrivateRoute: React.FC<PrivateRouteProps> = ({ children }) => {
  const { token, user } = useSelector((state: RootState) => state.auth);
  const location = useLocation();

  console.log('PrivateRoute auth state:', { token, user });

  if (!token || !user) {
    // 将用户重定向到登录页面，但保存他们试图访问的页面路径
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <>{children}</>;
};

export default PrivateRoute; 