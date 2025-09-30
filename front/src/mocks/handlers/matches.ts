import { http, HttpResponse, delay } from 'msw';

// 매칭 정보 조회
export const matchesHandlers = [
  http.get('/api/matches/:matchId', async ({ params }) => {
    const { matchId } = params;
    await delay(200);

    return HttpResponse.json({
      matchId: Number(matchId),
      relationType: 'FRIEND',
      startDate: '2025-09-11',
      dailyQuestionHour: 12,
      users: [
        { userId: 1, nickname: '사용자A' },
        { userId: 2, nickname: '사용자B' },
      ],
    });
  }),
  // 매칭 정보 업데이트(질문시간, 처음만난 날)
  http.patch('/api/matches/:matchId/info', async ({ request, params }) => {
    const body = (await request.json()) as { dailyQuestionHour: number; startDate: string | null };
    console.log('Mock PATCH /api/matches/:matchId/info', params.matchId, body);
    await delay(500);
    return HttpResponse.json(
      { message: '매칭 정보가 성공적으로 업데이트되었습니다.' },
      { status: 200 },
    );
  }),
];
