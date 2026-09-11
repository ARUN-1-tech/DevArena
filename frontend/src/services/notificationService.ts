import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/api';
import { NotificationItem } from '../types/social';

interface NotificationsPage {
  content: NotificationItem[];
  totalElements: number;
  totalPages: number;
  number: number;
}

export const notificationService = {
  async getNotifications(page = 0, size = 20): Promise<NotificationsPage> {
    const res = await apiClient.get<ApiResponse<NotificationsPage>>(
      `/notifications?page=${page}&size=${size}`
    );
    return res.data.data;
  },

  async getUnreadCount(): Promise<number> {
    const res = await apiClient.get<ApiResponse<{ unreadCount: number }>>('/notifications/unread-count');
    return res.data.data.unreadCount;
  },

  async markAsRead(id: string): Promise<NotificationItem> {
    const res = await apiClient.post<ApiResponse<NotificationItem>>(`/notifications/${id}/read`);
    return res.data.data;
  },

  async markAllAsRead(): Promise<void> {
    await apiClient.post<ApiResponse<void>>('/notifications/read-all');
  },
};
