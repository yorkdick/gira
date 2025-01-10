import { useSelector } from 'react-redux';
import { UserRole } from '@/types/auth';
import type { RootState } from '@/store';

export const usePermission = () => {
  const user = useSelector((state: RootState) => state.auth.user);

  const isAdmin = user?.role === UserRole.ADMIN;
  const isDeveloper = user?.role === UserRole.DEVELOPER;

  const canManageBoard = isAdmin;
  const canConfigureWIP = isAdmin;
  const canManageSprint = isAdmin;
  const canViewAllTasks = isAdmin;
  const canUpdateTask = (taskUserId: number) => isAdmin || user?.id === taskUserId;

  return {
    isAdmin,
    isDeveloper,
    canManageBoard,
    canConfigureWIP,
    canManageSprint,
    canViewAllTasks,
    canUpdateTask,
  };
}; 