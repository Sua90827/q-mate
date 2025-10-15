'use client';

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { fetchNotificationSettings, updateNotificationSettings } from '@/api/notification';
import { SuccessToast, ErrorToast } from '@/components/common/CustomToast';
import { useSubscribePush } from './useSubScription';

export const useNotificationSettings = () => {
  const queryClient = useQueryClient();
  const { subscribe } = useSubscribePush();

  // 조회
  const { data } = useQuery({
    queryKey: ['notificationSettings'],
    queryFn: fetchNotificationSettings,
    staleTime: 1000 * 60 * 5,
  });

  // 업데이트 (denied이면 설정 자체를 막고 서버 상태는 바꾸지 않음)
  const { mutateAsync: toggleNotification, isPending } = useMutation({
    mutationFn: async (enabled: boolean) => {
      if (enabled) {
        const hasWindow = typeof window !== 'undefined' && 'Notification' in window;
        const currentPermission: NotificationPermission = hasWindow
          ? Notification.permission
          : 'denied';

        // 권한이 'denied'면 대부분 브라우저에서 재요청 팝업이 뜨지 않지만,
        // 일부 환경(iOS/Safari 등) 대비 한 번 더 시도해 본다.
        // 여전히 허용되지 않으면 브라우저 설정 안내로 처리한다(서버 상태는 바꾸지 않음).
        if (currentPermission === 'denied') {
          const res = await Notification.requestPermission().catch(
            () => 'denied' as NotificationPermission,
          );
          if (res !== 'granted') {
            throw new Error(
              '브라우저 알림 권한이 차단되어 있어요. 브라우저 설정(사이트 권한)에서 허용해 주세요.',
            );
          }
        }

        let permission: NotificationPermission = currentPermission;
        if (currentPermission === 'default') {
          permission = await Notification.requestPermission();
          if (permission !== 'granted') {
            throw new Error('알림 권한을 허용하지 않아 설정을 변경할 수 없어요.');
          }
        }

        const result = await subscribe();
        if (!result.success) {
          throw new Error('푸시 구독에 실패했어요.');
        }

        await updateNotificationSettings(true);
        return true;
      }

      await updateNotificationSettings(false);
      return false;
    },
    onSuccess: (enabled) => {
      queryClient.invalidateQueries({ queryKey: ['notificationSettings'] });
      SuccessToast(enabled ? '알림이 켜졌어요.' : '알림이 꺼졌어요.');
    },
    onError: (err) => {
      const msg = err instanceof Error ? err.message : '알림 설정 변경에 실패했어요.';
      ErrorToast(msg);
    },
  });

  return { data, toggleNotification, isPending };
};
