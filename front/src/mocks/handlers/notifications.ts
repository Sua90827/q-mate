import { http, HttpResponse } from 'msw';
const ALL = Array.from({ length: 73 }).map((_, i) => ({
  notificationId: i + 1,
  category: i % 3 === 0 ? 'QUESTION' : i % 3 === 1 ? 'EVENT' : 'MATCH',
  code: i % 3 === 0 ? 'QI_TODAY_READY' : i % 3 === 1 ? 'EVENT_SAME_DAY' : 'MATCH_CONNECTED',
  listTitle: `알림 #${i + 1}`,
  createdAt: new Date(Date.now() - i * 3600_000).toISOString(),
  read: i % 5 === 0, //표시
}));
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

  //읽지 않은 알림개수
  http.get('/api/notifications/unread-count', async () => {
    return HttpResponse.json({
      count: 2,
    });
  }),
  //알림 리스트
  http.get('/api/notifications', async ({ request }) => {
    const url = new URL(request.url);
    const page = Number(url.searchParams.get('page') ?? '0');
    const size = Number(url.searchParams.get('size') ?? '20');

    const totalElements = ALL.length;
    const totalPages = size > 0 ? Math.ceil(totalElements / size) : 1;
    const start = page * size;
    const end = start + size;
    const content = size > 0 ? ALL.slice(start, end) : ALL;

    return HttpResponse.json({
      totalElements,
      totalPages,
      pageable: {
        paged: true,
        pageNumber: page,
        pageSize: size,
        offset: start,
        sort: { sorted: true, empty: false, unsorted: false },
        unpaged: false,
      },
      size,
      content,
      number: page,
      sort: { sorted: true, empty: false, unsorted: false },
      first: page === 0,
      last: page + 1 >= totalPages,
      numberOfElements: content.length,
      empty: content.length === 0,
    });
  }),
  //알림 삭제
  http.delete('/api/notifications/:notificationId(\\d+)', async ({ params }) => {
    const notificationId = Number(params.notificationId);

    if (!Number.isFinite(notificationId)) {
      return HttpResponse.json(
        { error: 'NOTIFICATION_NOT_FOUND', message: 'Notification not found' },
        { status: 404 },
      );
    }

    const exists = ALL.some((n) => n.notificationId === notificationId);
    if (!exists) {
      return HttpResponse.json(
        { error: 'NOTIFICATION_NOT_FOUND', message: 'Notification not found' },
        { status: 404 },
      );
    }

    return new HttpResponse(null, { status: 204 });
  }),
  //알림 상세 조회
  http.get('/api/notifications/:notificationId(\\d+)', async ({ params }) => {
    const { notificationId } = params;
    return HttpResponse.json({
      notificationId: 1,
      userId: 99,
      matchId: 1,
      category: 'QUESTION',
      code: 'QI_TODAY_READY',
      listTitle: '오늘의 질문이 도착했어요!',
      pushTitle: '새로운 질문이 도착했습니다!',
      resourceType: 'QUESTION_INSTANCE',
      resourceId: 10,
      readAt: '2025-10-04T07:00:16.315Z',
      createdAt: '2025-10-04T07:00:16.313Z',
    });
  }),

  // VAPID 공개키 조회
  http.get('/api/notifications/subscriptions/vapid-public-key', async () => {
    return HttpResponse.json({
      vapidPublicKey: 'BMockPublicKeyForTesting-1234567890',
    });
  }),
];
