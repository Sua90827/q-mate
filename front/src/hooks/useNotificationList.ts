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
import {
  useInfiniteQuery,
  useMutation,
  useQuery,
  useQueryClient,
  type InfiniteData,
  type QueryKey,
} from '@tanstack/react-query';

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

    getNextPageParam: (lastPage) => (lastPage.last ? undefined : lastPage.number + 1),
  });
};
//알림 상세
export const useNotificationDetail = (notificationId?: number) => {
  return useQuery<notificationResponseType>({
    queryKey: ['notificationDetail', notificationId],
    queryFn: () => fetchNotificationDetail(notificationId!),
    enabled: !!notificationId,
    staleTime: 1000 * 60 * 5,
  });
};
//알림 삭제
export const useDeleteNotification = () => {
  const queryClient = useQueryClient();

  type prevList = [QueryKey, InfiniteData<notificationListResponseType> | undefined][];

  return useMutation<unknown, Error, number, { prevList: prevList; prevUnread?: number }>({
    mutationFn: (notificationId: number) => deleteNotification(notificationId),

    onMutate: async (notificationId) => {
      await queryClient.cancelQueries({ queryKey: ['notificationsInfinite'] });

      const prevList = queryClient.getQueriesData<InfiniteData<notificationListResponseType>>({
        queryKey: ['notificationsInfinite'],
      });

      prevList.forEach(([key, data]) => {
        if (!data) return;

        const pages = data.pages.map((page) => {
          const existed = page.content.some((c) => c.notificationId === notificationId);
          if (!existed) return page;

          const content = page.content.filter((c) => c.notificationId !== notificationId);
          return {
            ...page,
            content,
            numberOfElements: Math.max(0, page.numberOfElements - (existed ? 1 : 0)),
            totalElements: Math.max(0, page.totalElements - (existed ? 1 : 0)),
            empty: content.length === 0,
          };
        });

        queryClient.setQueryData<InfiniteData<notificationListResponseType>>(key, {
          ...data,
          pages: pages as InfiniteData<notificationListResponseType>['pages'],
        });
      });

      const prevUnread = queryClient.getQueryData<number>(['unreadCount']);
      if (typeof prevUnread === 'number') {
        queryClient.setQueryData(['unreadCount'], Math.max(0, prevUnread - 1));
      }

      return { prevList, prevUnread };
    },

    onError: (_err, _vars, ctx) => {
      ctx?.prevList?.forEach(([key, data]) => {
        queryClient.setQueryData(key, data);
      });
      if (typeof ctx?.prevUnread === 'number') {
        queryClient.setQueryData(['unreadCount'], ctx.prevUnread);
      }
    },
    onSettled: () => {
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
