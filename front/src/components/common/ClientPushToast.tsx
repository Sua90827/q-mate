'use client';
import { useEffect } from 'react';
import { pushToast } from './CustomToast';
import { useRouter } from 'next/navigation';

function ClientPushToast() {
  const router = useRouter();
  useEffect(() => {
    if (!navigator?.serviceWorker) return;

    const handler = (event: MessageEvent) => {
      const { data } = event;
      if (!data || typeof data !== 'object') return;
      if (data.type !== 'PUSH_MESSAGE') return;

      const payload = data.payload ?? {};
      const title = payload.title ?? '알림이도착했어요';
      const body = payload.body ?? '테스트용';

      pushToast(title, body, () => router.push('/main'));
    };

    navigator.serviceWorker.addEventListener('message', handler);
    return () => navigator.serviceWorker.removeEventListener('message', handler);
  }, [router]);
  return null;
}

export default ClientPushToast;
