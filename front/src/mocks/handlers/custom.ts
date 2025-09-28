import { http, HttpResponse, delay } from 'msw';

export const customHandlers = [
  // 커스텀 질문 목록 조회
  http.get('/api/matches/:matchId/custom-questions', async ({ params }) => {
    const { matchId } = params;
    await delay(200);

    const content = [
      {
        customQuestionId: 901,
        sourceType: 'CUSTOM',
        relationType: 'COUPLE',
        matchId: Number(matchId),
        text: '연인이 가장 좋아하는 음식은?',
        isEditable: true,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      },
    ];

    return HttpResponse.json({
      content,
      pageable: {
        sort: { empty: true, sorted: false, unsorted: true },
        offset: 0,
        pageNumber: 0,
        pageSize: 20,
        paged: true,
        unpaged: false,
      },
      totalPages: 1,
      totalElements: content.length,
      last: true,
      size: 20,
      number: 0,
      sort: { empty: true, sorted: false, unsorted: true },
      numberOfElements: content.length,
      first: true,
      empty: content.length === 0,
    });
  }),

  // 커스텀 질문 등록 (유효성: 문자열, 1~100자)
  http.post('/api/matches/:matchId/custom-questions', async ({ params, request }) => {
    const { matchId } = params;
    const body = (await request.json()) as { text?: string };

    const text = typeof body?.text === 'string' ? body.text.trim() : '';
    if (!text || text.length > 100) {
      return HttpResponse.json(
        { error: 'FIELD_VALIDATION_FAILED', message: 'text(1~100자)이 필요합니다.' },
        { status: 400 },
      );
    }

    await delay(200);
    const id = Math.floor(Math.random() * 10000);
    const now = new Date().toISOString();
    return HttpResponse.json(
      {
        customQuestionId: id,
        sourceType: 'CUSTOM',
        relationType: 'COUPLE',
        matchId: Number(matchId),
        text,
        isEditable: true,
        createdAt: now,
        updatedAt: now,
      },
      { status: 201 },
    );
  }),

  // 커스텀 질문 수정 (유효성: 문자열, 1~100자)
  http.patch('/api/custom-questions/:id', async ({ params, request }) => {
    const { id } = params;
    const body = (await request.json()) as { text?: string };

    const text = typeof body?.text === 'string' ? body.text.trim() : '';
    if (!text || text.length > 100) {
      return HttpResponse.json(
        { error: 'FIELD_VALIDATION_FAILED', message: 'text(1~100자)이 필요합니다.' },
        { status: 400 },
      );
    }

    await delay(200);
    return HttpResponse.json({
      customQuestionId: Number(id),
      sourceType: 'CUSTOM',
      relationType: 'COUPLE',
      matchId: 10,
      text,
      isEditable: true,
      createdAt: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
      updatedAt: new Date().toISOString(),
    });
  }),

  // 커스텀 질문 삭제
  http.delete('/api/custom-questions/:id', async () => {
    await delay(150);
    return new HttpResponse(null, { status: 204 });
  }),

  // 커스텀 질문 단건 조회 (키: customQuestionId 로 정규화)
  http.get('/api/custom-questions/:id', async ({ params }) => {
    const { id } = params;
    await delay(150);

    return HttpResponse.json({
      customQuestionId: Number(id),
      sourceType: 'CUSTOM',
      relationType: 'COUPLE',
      category: { id: 5, name: '일상' },
      text: '커스텀 질문',
      isEditable: true,
      updatedAt: new Date().toISOString(),
    });
  }),
];
