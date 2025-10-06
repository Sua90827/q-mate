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

export const usePushSubscription = () => {
  const ensureVapidPublicKey = useVapidPublicKeyStore((state) => state.ensureVapidPublicKey);
  const { mutateAsync: sendSubscription, isPending } = useMutation({
    mutationFn: fetchSubscription,
  });

  const ensurePushSubscribed = useCallback(async () => {
    if (!isPushSupported())
      return {
        success: false,
        message: '브라우저에서 지원하지 않는 기능입니다.',
      };
    const permission = await requestNotificationPermission();
    if (permission !== 'granted')
      return {
        success: false,
        message: '브라우저 권한을 확인해주세요.',
      };
    const registration = await getServiceWorkerRegistration();
    if (!registration) {
      return {
        success: false,
        message: 'SW 준비에 실패했습니다.',
      };
    }
    //기존 구독확인
    const existingSubscription = await getExistingSubscription(registration);
    if (existingSubscription) {
      await sendSubscription(toSubscriptionJSON(existingSubscription));
      return {
        success: true,
        created: false,
      };
    }
    //구독 새로 생성
    const vapidPublicKey = await ensureVapidPublicKey();
    const newSubscription = await subscribePush(registration, vapidPublicKey);
    await sendSubscription(toSubscriptionJSON(newSubscription));
    return {
      success: true,
      created: true,
    };
  }, [ensureVapidPublicKey, sendSubscription]);
  return { ensurePushSubscribed, isPending };
};
