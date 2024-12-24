import api from './api';

export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  avatar: string;
  roles: string[];
  status: number;
  enabled: boolean;
}

export interface UpdateUserRequest {
  username?: string;
  email?: string;
  fullName?: string;
  avatar?: string;
}

export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

class UserService {
  async getCurrentUser() {
    const response = await api.get<User>('/users/me');
    return response.data;
  }

  async getUserById(id: number) {
    const response = await api.get<User>(`/users/${id}`);
    return response.data;
  }

  async getUsers(page = 0, size = 10) {
    const response = await api.get<{ content: User[]; totalElements: number }>(
      `/users?page=${page}&size=${size}`
    );
    return response.data;
  }

  async updateUser(id: number, data: UpdateUserRequest) {
    const response = await api.put<User>(`/users/${id}`, data);
    return response.data;
  }

  async updateAvatar(id: number, file: File) {
    const formData = new FormData();
    formData.append('avatar', file);

    const response = await api.put<User>(`/users/${id}/avatar`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  }

  async changePassword(id: number, data: ChangePasswordRequest) {
    await api.put(`/users/${id}/password`, data);
  }

  async enableUser(id: number) {
    const response = await api.put<User>(`/users/${id}/enable`);
    return response.data;
  }

  async disableUser(id: number) {
    const response = await api.put<User>(`/users/${id}/disable`);
    return response.data;
  }
}

export default new UserService(); 