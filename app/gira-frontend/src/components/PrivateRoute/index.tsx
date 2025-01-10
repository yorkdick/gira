import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { usePermission } from '@/hooks/usePermission';
import { UserRole } from '@/types/auth';

interface PrivateRouteProps {
  children: React.ReactNode;
  requiredRole?: UserRole;
}

export const PrivateRoute: React.FC<PrivateRouteProps> = ({ children, requiredRole }) => {
  const location = useLocation();
  const { isAdmin, isDeveloper } = usePermission();
  const isAuthenticated = isAdmin || isDeveloper;

  // 未登录重定向到登录页
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // 权限不足重定向到403页面
  if (requiredRole === UserRole.ADMIN && !isAdmin) {
    return <Navigate to="/403" replace />;
  }

  return <>{children}</>;
}; 