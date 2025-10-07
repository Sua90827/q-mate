'use client';

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { fetchNotificationSettings, updateNotificationSettings } from '@/api/notification';

import { SuccessToast, ErrorToast } from '@/components/common/CustomToast';
import { useSubscribePush } from './useSubScription';
import { useUnsubscribePush } from './useUnSubScription';

export const useNotificationSettings = () => {
  const queryClient = useQueryClient();
  const { subscribe } = useSubscribePush();
  const { unsubscribe } = useUnsubscribePush();

  // 조회
  const { data } = useQuery({
    queryKey: ['notificationSettings'],
    queryFn: fetchNotificationSettings,
    staleTime: 1000 * 60 * 5,
    // enabled: 조건을 사용자가 있을때?
  });

  // 업데이트
  const { mutateAsync: toggleNotification, isPending } = useMutation({
    mutationFn: async (enabled: boolean) => {
      if (enabled) {
        const result = await subscribe();
        if (!result.success) {
          ErrorToast(result.message);
        }
        await updateNotificationSettings(true);
        return true;
      }

      // try {
      //   await unsubscribe({ subscriptionId: data?.subscriptionId });
      // } catch {}
      await updateNotificationSettings(false);
      return false;
    },
    onSuccess: (enabled) => {
      queryClient.invalidateQueries({ queryKey: ['notificationSettings'] });
      SuccessToast(enabled ? '알림이 켜졌어요.' : '알림이 꺼졌어요.');
    },
    onError: () => {
      ErrorToast('알림 설정 변경에 실패했어요.');
    },
  });

  return { data, toggleNotification, isPending };
};
