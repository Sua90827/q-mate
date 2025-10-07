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
