import { EventMonthResponse } from '@/types/scheduleType';
import { http, HttpResponse } from 'msw';

interface ScheduleRequestBody {
  title: string;
  description?: string;
  eventAt: string;
  repeatType: 'NONE' | 'WEEKLY' | 'MONTHLY' | 'YEARLY';
  alarmOption: 'NONE' | 'WEEK_BEFORE' | 'THREE_DAYS_BEFORE' | 'SAME_DAY';
  isAnniversary?: boolean;
}

const events = [
  {
    eventId: 1,
    title: '회의',
    description: '팀 회의',
    eventAt: '2025-10-20',
    repeatType: 'NONE',
    alarmOption: 'SAME_DAY',
    isAnniversary: false,
    createdAt: '2025-09-10T09:00:00',
    updatedAt: '2025-09-15T12:00:00',
  },
  {
    eventId: 2,
    title: '부모님 기념일',
    description: '점심 약속',
    eventAt: '2025-10-25',
    repeatType: 'YEARLY',
    alarmOption: 'SAME_DAY',
    isAnniversary: true,
    createdAt: '2025-09-01T10:00:00',
    updatedAt: '2025-09-20T08:00:00',
  },
  {
    eventId: 3,
    title: '기념일 저녁',
    description: '19:00 예약',
    eventAt: '2025-10-09',
    repeatType: 'YEARLY',
    alarmOption: 'SAME_DAY',
    isAnniversary: true,
    createdAt: '2025-09-20T12:32:11',
    updatedAt: '2025-09-28T09:10:01',
  },
  {
    eventId: 4,
    title: '기념일 저녁',
    description: '19:00 예약',
    eventAt: '2025-10-20',
    repeatType: 'YEARLY',
    alarmOption: 'SAME_DAY',
    isAnniversary: true,
    createdAt: '2025-09-20T12:32:11',
    updatedAt: '2025-09-28T09:10:01',
  },
  {
    eventId: 5,
    title: '일정',
    description: '19:00 예약',
    eventAt: '2025-09-29',
    repeatType: 'YEARLY',
    alarmOption: 'SAME_DAY',
    isAnniversary: false,
    createdAt: '2025-09-20T12:32:11',
    updatedAt: '2025-09-28T09:10:01',
  },
];

export const scheduleHandlers = [
  // 조회
  http.get('/api/matches/:matchId/events', async () => {
    return HttpResponse.json(
      {
        content: events,
        pageable: {
          pageNumber: 0,
          pageSize: 20,
          sort: [{ property: 'eventAt', direction: 'ASC' }],
        },
        totalElements: events.length,
        totalPages: 1,
        last: true,
        first: true,
        numberOfElements: events.length,
        empty: events.length === 0,
      },
      { status: 200 },
    );
  }),
  // 월별 조회

  http.get('/api/matches/:matchId/events/calendar', ({ request, params }) => {
    const url = new URL(request.url);
    const from = url.searchParams.get('from');
    const to = url.searchParams.get('to');

    if (!from || !to) {
      return HttpResponse.json({ error: 'from, to required' }, { status: 400 });
    }

    const fromDate = new Date(from);
    const toDate = new Date(to);
    const diffDays = Math.ceil((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));

    if (diffDays > 60) {
      return HttpResponse.json(
        { error: '조회 기간은 최대 60일까지만 허용합니다.' },
        { status: 400 },
      );
    }
    const year = fromDate.getFullYear();
    const month = fromDate.getMonth() + 1;
    const allEvents = [
      { eventId: 1, eventAt: '2025-10-25', anniversary: false },
      { eventId: 2, eventAt: '2025-10-29', anniversary: true },
      { eventId: 3, eventAt: '2025-10-20', anniversary: true },
      { eventId: 4, eventAt: '2025-10-9', anniversary: true },
      { eventId: 5, eventAt: '2025-09-25', anniversary: false },
    ];

    const filteredDays = allEvents.filter((day) => {
      return day.eventAt >= from && day.eventAt <= to;
    });

    const mockData: EventMonthResponse = {
      year,
      month,
      days: filteredDays,
    };

    return HttpResponse.json(mockData);
  }),
  // 일정 상세 조회
  http.get('/api/matches/:matchId/events/:eventId', ({ params }) => {
    const eventId = Number(params.eventId);
    const event = events.find((e) => e.eventId === eventId);
    if (!event) {
      return HttpResponse.json({ message: 'Not found' }, { status: 404 });
    }
    return HttpResponse.json(event, { status: 200 });
  }),

  // 등록
  http.post('/api/matches/:matchId/events', async ({ request }) => {
    const body = (await request.json()) as ScheduleRequestBody;

    if (!body.title || !body.eventAt || !body.repeatType || !body.alarmOption) {
      return HttpResponse.json({ message: '필수 값이 누락되었습니다.' }, { status: 400 });
    }

    const newEvent = {
      eventId: events.length ? events[events.length - 1].eventId + 1 : 1,
      title: body.title,
      description: body.description ?? '',
      eventAt: body.eventAt,
      repeatType: body.repeatType,
      alarmOption: body.alarmOption,
      isAnniversary: body.isAnniversary ?? false,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };

    events.push(newEvent);
    return HttpResponse.json(newEvent, { status: 201 });
  }),

  // 삭제
  http.delete('/api/matches/:matchId/events/:eventId(\\d+)', async ({ params }) => {
    const eventId = Number(params.eventId);
    const index = events.findIndex((e) => e.eventId === eventId);

    if (index === -1) {
      return HttpResponse.json({ message: 'Not found' }, { status: 404 });
    }

    const [deleted] = events.splice(index, 1);
    return HttpResponse.json(deleted, { status: 200 });
  }),
  // 수정
  http.patch('/api/matches/:matchId/events/:eventId', async ({ params, request }) => {
    const eventId = Number(params.eventId);
    const body = (await request.json()) as ScheduleRequestBody;

    const idx = events.findIndex((e) => e.eventId === eventId);
    if (idx === -1) {
      return HttpResponse.json({ message: 'Not found' }, { status: 404 });
    }

    const target = events[idx];
    const updated = {
      ...target,
      title: body.title,
      description: body.description ?? '',
      eventAt: body.eventAt,
      repeatType: body.repeatType,
      alarmOption: body.alarmOption,
      updatedAt: new Date().toISOString(),
    };

    events[idx] = updated;
    return HttpResponse.json(updated, { status: 200 });
  }),
];
