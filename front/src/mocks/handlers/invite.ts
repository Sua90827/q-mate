import { http, HttpResponse } from 'msw';

export const inviteHandlers = [
  //초대 코드 발급
  http.post('/api/matches', async ({ request }) => {
    const body = await request.json();
    const { relationType, startDate } = body as {
      relationType: 'COUPLE' | 'FRIEND';
      startDate: string;
    };

    await new Promise((res) => setTimeout(res, 300));

    return HttpResponse.json({
      inviteCode: `${relationType}_${Math.random().toString(36).substring(2, 8).toUpperCase()}`,
      matchId: Math.floor(Math.random() * 1000) + 1,
    });
  }),

  //초대 코드 입력 (매칭)
  http.post('/api/matches/join', async ({ request }) => {
    const body = await request.json();
    const { inviteCode } = body as {
      inviteCode: string;
    };

    await new Promise((res) => setTimeout(res, 300));

    return HttpResponse.json({
      matchId: Math.floor(Math.random() * 1000) + 1,
      message: '매칭이 완료되었습니다.',
      partnerNickname: '활기찬 고래',
    });
  }),

  //초대 유효성 검사

  http.post('/api/invites/validate', async ({ request }) => {
    const body = (await request.json()) as { inviteCode: string };

    if (body.inviteCode === 'COUPLE_V7YZXG') {
      return HttpResponse.json({ valid: true, inviteCode: body.inviteCode }, { status: 200 });
    }

    return HttpResponse.json(
      { valid: false, message: '유효하지 않은 초대코드입니다.' },
      { status: 400 },
    );
  }),
];
