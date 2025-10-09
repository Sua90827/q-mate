import {
  deleteNotification,
  fetchNotificationDetail,
  fetchNotifications,
  fetchUnreadCount,
} from '@/api/notification';
import {
  ListParams,
  notificationListResponseType,
  notificationResponseType,
} from '@/types/notification';
import { useInfiniteQuery, useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

//알림 리스트
export const useNotifications = (params: ListParams) => {
  return useQuery({
    queryKey: ['notifications', params],
    queryFn: () => fetchNotifications(params),
  });
};

export const useInfiniteNotifications = (params: ListParams = { size: 20, page: 0 }) => {
  return useInfiniteQuery<notificationListResponseType, Error>({
    queryKey: ['notificationsInfinite', params],
    initialPageParam: 0,
    queryFn: ({ pageParam }) =>
      fetchNotifications({
        ...params,
        page: pageParam as number,
      }),
    // lastPage.number / lastPage.last 로 다음 페이지 계산
    getNextPageParam: (lastPage) => (lastPage.last ? undefined : lastPage.number + 1),

    refetchOnWindowFocus: false,
  });
};
//알림 상세
export const useNotificationDetail = (notificationId?: number) => {
  return useQuery<notificationResponseType>({
    queryKey: ['notificationDetail', notificationId],
    queryFn: () => fetchNotificationDetail(notificationId!),
    enabled: !!notificationId,
    //알림 상세는 캐싱이 필요할거라 생각이되어 작성
    staleTime: 1000 * 60 * 5,
  });
};
//알림 삭제
export const useDeleteNotification = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (notificationId: number) => deleteNotification(notificationId),
    onSuccess: () => {
      // 무한스크롤목록 /안 읽은 카운트 최신화

      queryClient.invalidateQueries({ queryKey: ['notificationsInfinite'] });
      queryClient.invalidateQueries({ queryKey: ['unreadCount'] });
    },
  });
};
//읽지 않은 알림
export const useUnreadCount = () => {
  return useQuery({
    queryKey: ['unreadCount'],
    queryFn: fetchUnreadCount,
  });
};
