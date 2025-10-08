'use client';

import { useCallback } from 'react';
import { useMutation } from '@tanstack/react-query';
import {
  isPushSupported,
  getServiceWorkerRegistration,
  getExistingSubscription,
} from '@/utils/push';
import { fetchUnSubscriptionByEndpoint, fetchUnSubscriptionById } from '@/api/notification';

export type UnsubscribeResult =
  | { ok: true; used: 'endpoint' | 'id' | null }
  | { ok: false; used: null };

export const useUnsubscribePush = () => {
  const { mutateAsync: unSubByEndpoint } = useMutation({
    mutationFn: fetchUnSubscriptionByEndpoint,
  });
  const { mutateAsync: unSubById } = useMutation({
    mutationFn: fetchUnSubscriptionById,
  });

  const unsubscribe = useCallback(
    async (opts?: { subscriptionId?: number }): Promise<UnsubscribeResult> => {
      if (!isPushSupported()) return { ok: true, used: null };

      try {
        const reg = await getServiceWorkerRegistration();
        if (!reg) return { ok: true, used: null };

        let used: 'endpoint' | 'id' | null = null;

        // endpoint 기반 해지
        const sub = await getExistingSubscription(reg);
        if (sub) {
          const endpoint = sub.endpoint;
          try {
            await sub.unsubscribe();
          } catch {}
          if (endpoint) {
            await unSubByEndpoint(endpoint);
            used = 'endpoint';
          }
        }

        // id 기반 해지
        if (opts?.subscriptionId) {
          await unSubById(opts.subscriptionId);
          used = used ?? 'id';
        }

        return { ok: true, used };
      } catch {
        return { ok: false, used: null };
      }
    },
    [unSubByEndpoint, unSubById],
  );

  return { unsubscribe };
};
