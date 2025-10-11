import {
  categoryType,
  codeType,
  notificationListResponseType,
  sortType,
  subScriptionsType,
  vapidPublicKeyType,
} from '@/types/notification';
import instance from '../lib/axiosInstance';

//푸시 알림 구독 갱신
export const fetchSubscription = async (subscription: subScriptionsType) => {
  const res = await instance.post('api/notifications/subscriptions', subscription);
  return res.data;
};

//vapid-public-key 조회
export const fetchVapidPublicKey = async () => {
  const res = await instance.get('/notifications/subscriptions/vapid-public-key');
  return res.data as vapidPublicKeyType;
};
//푸시 알림 구독 해지(ID기반)
export const fetchUnSubscriptionById = async (subscriptionId: number) => {
  const res = await instance.delete(`/notifications/subscriptions/${subscriptionId}`);
  return res.data;
};
//푸시 알림 구독 해지(endpoint기반)
export const fetchUnSubscriptionByEndpoint = async (endpoint: string) => {
  const res = await instance.delete('/notifications/subscriptions/by-endpoint', {
    params: { endpoint },
  });
  return res.data;
};
//알림 설정 조회
export const fetchNotificationSettings = async () => {
  const res = await instance.get('/notifications/settings');
  return res.data;
};
//알림 설정 수정
export const updateNotificationSettings = async (pushEnabled: boolean) => {
  const res = await instance.patch('/notifications/settings', { pushEnabled });
  return res.data;
};

//알림 리스트 조회
export const fetchNotifications = async (params?: {
  category?: categoryType;
  code?: codeType;
  unread?: boolean;
  page?: number;
  size?: number;
  sort?: sortType;
}) => {
  const res = await instance.get<notificationListResponseType>('/notifications', { params });
  return res.data;
};
// 알림상세 조회
export const fetchNotificationDetail = async (notificationId: number) => {
  const res = await instance.get(`/api/notifications/${notificationId}`);
  return res.data;
};
// 알림 삭제
export const deleteNotification = async (notificationId: number) => {
  const res = await instance.delete(`/api/notifications/${notificationId}`);
  return res.data;
};
//읽지않은 알림갯수
export const fetchUnreadCount = async () => {
  const res = await instance.get('/api/notifications/unread-count');
  return res.data;
};
