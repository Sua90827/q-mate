'use client';

import { useCallback } from 'react';
import { useMutation } from '@tanstack/react-query';
import {
  isPushSupported,
  requestNotificationPermission,
  getServiceWorkerRegistration,
  getExistingSubscription,
  subscribePush,
  toSubscriptionJSON,
} from '@/utils/push';
import { fetchSubscription } from '@/api/notification';
import { useVapidPublicKeyStore } from '@/store/useVapidPublicKeyStore';

export type SubscribeResult =
  | { success: true; created: boolean; subscriptionId?: number }
  | { success: false; message: string };

type SubscriptionUpsertResponse = { subscriptionId: number };

export const useSubscribePush = () => {
  const ensureVapidPublicKey = useVapidPublicKeyStore((s) => s.ensureVapidPublicKey);

  // 서버에 구독 정보(등록/갱신) 전송
  const { mutateAsync: upsertSubscription, isPending } = useMutation<
    SubscriptionUpsertResponse,
    Error,
    ReturnType<typeof toSubscriptionJSON>
  >({
    mutationFn: fetchSubscription,
  });

  const subscribe = useCallback(async (): Promise<SubscribeResult> => {
    if (!isPushSupported())
      return { success: false, message: '브라우저에서 지원하지 않는 기능입니다.' };

    const permission = await requestNotificationPermission();
    if (permission !== 'granted')
      return { success: false, message: '브라우저 알림 권한을 허용해주세요.' };

    const reg = await getServiceWorkerRegistration();
    if (!reg) return { success: false, message: '서비스워커 준비에 실패했습니다.' };

    // 1) 기존 구독 → 서버에 갱신
    const existing = await getExistingSubscription(reg);
    if (existing) {
      const res = await upsertSubscription(toSubscriptionJSON(existing));
      return { success: true, created: false, subscriptionId: res.subscriptionId };
    }

    // 2) 신규 구독 생성 → 서버에 등록
    const vapidPublicKey = await ensureVapidPublicKey();
    const sub = await subscribePush(reg, vapidPublicKey);
    const res = await upsertSubscription(toSubscriptionJSON(sub));
    return { success: true, created: true, subscriptionId: res.subscriptionId };
  }, [ensureVapidPublicKey, upsertSubscription]);

  return { subscribe, isPending };
};
