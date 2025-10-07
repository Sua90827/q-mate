import { http, HttpResponse } from 'msw';

export const notificationsHandler = [
  //알림 설정 조회
  http.get('/api/notifications/settings', async () => {
    return HttpResponse.json({
      pushEnabled: false,
    });
  }),
  //알림 설정 수정
  http.patch('/api/notifications/settings', async ({ request }) => {
    const body = (await request.json()) as { pushEnabled: boolean };
    return HttpResponse.json({
      pushEnabled: body.pushEnabled,
    });
  }),

  //알림 리스트 조회
  http.get('/api/notifications', async ({ params }) => {
    return HttpResponse.json({
      content: [
        {
          notificationId: 1,
          category: 'QUESTION',
          code: 'QI_TODAY_READY',
          listTitle: '오늘의 질문이 도착했어요!',
          createdAt: '2025-10-04T07:00:16.313Z',
          read: true,
        },
        {
          notificationId: 2,
          category: 'EVENT',
          code: 'EVENT_SAME_DAY',
          listTitle: '오늘은 기념일이에요 🎉',
          createdAt: '2025-10-03T10:32:45.100Z',
          read: false,
        },
        {
          notificationId: 3,
          category: 'MATCH',
          code: 'MATCH_CONNECTED',
          listTitle: '새로운 매칭이 연결되었어요.',
          createdAt: '2025-10-02T22:11:00.000Z',
          read: false,
        },
      ],
    });
  }),
  //알림 상세 조회
  http.get('/api/notifications/:notificationId', async ({ params }) => {
    return HttpResponse.json({
      notificationId: 1,
      userId: 99,
      matchId: 1,
      category: 'QUESTION',
      code: 'QI_TODAY_READY',
      listTitle: '오늘의 질문이 도착했어요!',
      pushTitle: '새로운 질문이 도착했습니다!',
      resourceType: 'QUESTION_INSTANCE',
      resourceId: 123,
      readAt: '2025-10-04T07:00:16.315Z',
      createdAt: '2025-10-04T07:00:16.313Z',
    });
  }),
  //읽지 않은 알림개수
  http.get('/api/notifications/unread-count', async () => {
    return HttpResponse.json({
      count: 2,
    });
  }), // VAPID 공개키 조회
  http.get('/api/notifications/subscriptions/vapid-public-key', async () => {
    return HttpResponse.json({
      vapidPublicKey: 'BMockPublicKeyForTesting-1234567890',
    });
  }),
];
