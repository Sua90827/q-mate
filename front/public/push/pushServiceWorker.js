// 설치
self.addEventListener('install', (event) => {
  console.log('[push-sw] installed');
  self.skipWaiting();
});

// 활성화
self.addEventListener('activate', (event) => {
  console.log('[push-sw] activated');
  event.waitUntil(self.clients.claim());
});

// 푸시 알림 수신
self.addEventListener('push', (event) => {
  let data = {};
  try {
    data = event.data?.json() ?? {};
  } catch {
    data = { title: '알림이도착했습니다.', body: event.data?.text() ?? '' };
  }

  const title = data.title ?? '알림이도착했습니다.';
  const options = {
    body: data.body ?? '',
    icon: data.icon ?? '/favicon.svg',
    data: data.data ?? {},
  };

  // 클라이언트가 열려있는지 확인해서 분기
  event.waitUntil(
    clients.matchAll({ type: 'window', includeUncontrolled: true }).then((clientList) => {
      const visibleClient = clientList.find((c) => c.visibilityState === 'visible');

      if (visibleClient) {
        // 포그라운드 → 메시지를 클라이언트로 전달 (Toast에서 처리)
        visibleClient.postMessage({
          type: 'PUSH_MESSAGE',
          payload: { title, ...options },
        });
      } else {
        // 백그라운드 → OS 알림 띄우기
        self.registration.showNotification(title, options);
      }
    }),
  );
});

// 알림 클릭 시 동작 수정필요
self.addEventListener('notificationclick', (event) => {
  event.notification.close();
  const url = event.notification.data?.url ?? '/';
  event.waitUntil(
    clients.matchAll({ type: 'window', includeUncontrolled: true }).then((clientList) => {
      for (const client of clientList) {
        if ('focus' in client) {
          client.navigate(url);
          return client.focus();
        }
      }
      return clients.openWindow(url);
    }),
  );
});
