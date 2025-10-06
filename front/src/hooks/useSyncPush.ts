'use client';

import { useCallback } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { fetchNotificationSettings, updateNotificationSettings } from '@/api/notification';
import { useSubscribePush } from '@/hooks/useSubScription';

export function useSyncPushOnLogin() {
  const queryClient = useQueryClient();
  const { subscribe } = useSubscribePush();

  return useCallback(async () => {
    const server = await queryClient.fetchQuery({
      queryKey: ['notificationSettings'],
      queryFn: fetchNotificationSettings,
    });

    const permission: NotificationPermission =
      typeof window !== 'undefined' && 'Notification' in window
        ? Notification.permission
        : 'denied';

    if (server.pushEnabled === true) {
      if (permission === 'granted') {
        await subscribe();
      } else if (permission === 'default') {
        const result = await Notification.requestPermission();
        if (result === 'granted') {
          await subscribe();
        } else {
          await updateNotificationSettings(false);

          queryClient.invalidateQueries({ queryKey: ['notificationSettings'] });
        }
      } else {
        await updateNotificationSettings(false);
        queryClient.invalidateQueries({ queryKey: ['notificationSettings'] });
      }
    }
  }, [subscribe, queryClient]);
}
