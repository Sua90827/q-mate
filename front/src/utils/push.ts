// 기능 지원 여부 확인
// 브라우저가 3가지를 모두 지원해야 푸시 사용 가능
export const isPushSupported = () =>
  typeof window !== 'undefined' &&
  'serviceWorker' in navigator &&
  'Notification' in window &&
  'PushManager' in window;

//사용자에게 알림 권한을 요청
export const requestNotificationPermission = async (): Promise<NotificationPermission> => {
  if (!('Notification' in window)) return 'denied';
  if (Notification.permission === 'granted') return 'granted';
  if (Notification.permission === 'denied') return 'denied';

  return await Notification.requestPermission();
};

export const getServiceWorkerRegistration = async (): Promise<ServiceWorkerRegistration | null> => {
  if (!('serviceWorker' in navigator)) return null;
  try {
    const reg = await navigator.serviceWorker.getRegistration('/push/');
    console.log('[Push] Service Worker ready:', reg);
    return reg ?? null;
  } catch (error) {
    console.log('error', error);
    return null;
  }
};

export const getExistingSubscription = async (reg: ServiceWorkerRegistration) => {
  try {
    return await reg.pushManager.getSubscription();
  } catch {
    return null;
  }
};
export const subscribePush = async (reg: ServiceWorkerRegistration, vapidPublicKey: string) => {
  const applicationServerKey = urlBase64ToUint8Array(vapidPublicKey);
  return await reg.pushManager.subscribe({
    userVisibleOnly: true,
    applicationServerKey,
  });
};
//푸시 구독 해지

export const getEndpointFromSubscription = (sub?: PushSubscription | null) => sub?.endpoint ?? null;

export const unsubscribeIfExists = async (
  reg: ServiceWorkerRegistration,
  opts?: {
    subscriptionId?: number;
    serverUnsubscribeByEndpoint?: (endpoint: string) => Promise<unknown>;
    serverUnsubscribeById?: (id: number) => Promise<unknown>;
  },
): Promise<{ ok: boolean; used: 'endpoint' | 'id' | null; value?: string | number }> => {
  try {
    const sub = await reg.pushManager.getSubscription();
    const endpoint = getEndpointFromSubscription(sub);

    if (sub) {
      try {
        await sub.unsubscribe();
      } catch (error) {
        console.log('구독해지실패', error);
      }
    }

    // endpoint 가 있을 때
    if (endpoint && opts?.serverUnsubscribeByEndpoint) {
      await opts.serverUnsubscribeByEndpoint(endpoint);
      return { ok: true, used: 'endpoint', value: endpoint };
    }

    // endpoint 가 없을 때 id 로 해지
    if (!endpoint && typeof opts?.subscriptionId === 'number' && opts?.serverUnsubscribeById) {
      await opts.serverUnsubscribeById(opts.subscriptionId);
      return { ok: true, used: 'id', value: opts.subscriptionId };
    }

    // 서버에 통보할 정보가 없었음
    return { ok: true, used: null };
  } catch {
    return { ok: false, used: null };
  }
};

//데이터 형태변환
// 공개키 변환
const urlBase64ToUint8Array = (base64: string) => {
  const padding = '='.repeat((4 - (base64.length % 4)) % 4);
  const base64Safe = (base64 + padding).replace(/-/g, '+').replace(/_/g, '/');
  const raw = atob(base64Safe);
  const out = new Uint8Array(raw.length);
  for (let i = 0; i < raw.length; i++) out[i] = raw.charCodeAt(i);
  return out;
};
// Subscription json변환
export const toSubscriptionJSON = (sub: PushSubscription) => {
  return sub.toJSON() as {
    endpoint: 'string';
    keyP256dh: 'string';
    keyAuth: 'string';
    keys: {
      p256dh: 'string';
      auth: 'string';
    };
  };
};
