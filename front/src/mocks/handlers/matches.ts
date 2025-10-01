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
      status: 'DETACHED_PENDING_DELETE',
      // status: 'Active',
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
  //매칭 연결 끊기
  http.post('/api/matches/:matchId/disconnect', async ({ params }) => {
    const { matchId } = params;
    console.log('Mock POST /api/matches/:matchId/disconnect', matchId);
    await delay(200);
    return HttpResponse.json(
      { message: '매칭 연결 끊기 요청이 처리되었습니다.2주 후 데이터가 삭제됩니다.' },
      { status: 200 },
    );
  }),
  //매칭 연결 복구
  http.post('/api/matches/:matchId/restore', async ({ params }) => {
    const { matchId } = params;
    console.log('Mock POST /api/matches/:matchId/restore', matchId);
    await delay(200);
    return HttpResponse.json({ message: '매칭이 성공적으로 복구되었습니다.' }, { status: 200 });
  }),
];
