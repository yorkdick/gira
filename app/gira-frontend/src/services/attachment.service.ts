import api from './api';

export interface Attachment {
  id: number;
  filename: string;
  contentType: string;
  size: number;
  path: string;
  taskId: number;
  user: {
    id: number;
    username: string;
    fullName: string;
    avatar: string;
  };
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  updatedBy: string;
}

class AttachmentService {
  async uploadAttachment(taskId: number, file: File) {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('taskId', taskId.toString());

    const response = await api.post<Attachment>('/attachments', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  }

  async getAttachment(id: number) {
    const response = await api.get<Attachment>(`/attachments/${id}`);
    return response.data;
  }

  async getAttachmentsByTask(taskId: number) {
    const response = await api.get<Attachment[]>(`/attachments/task/${taskId}`);
    return response.data;
  }

  async downloadAttachment(id: number) {
    const response = await api.get(`/attachments/${id}/download`, {
      responseType: 'blob',
    });
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', response.headers['content-disposition'].split('filename=')[1].replace(/"/g, ''));
    document.body.appendChild(link);
    link.click();
    link.remove();
  }

  async deleteAttachment(id: number) {
    await api.delete(`/attachments/${id}`);
  }
}

export default new AttachmentService(); 