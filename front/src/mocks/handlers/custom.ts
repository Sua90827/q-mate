import { http, HttpResponse } from 'msw';

// 전역 배열 (조회/등록/수정/삭제 시 공유)
let customQuestions: {
  customQuestionId: number;
  sourceType: 'CUSTOM';
  relationType: 'COUPLE';
  matchId: number;
  text: string;
  isEditable: boolean;
  createdAt: string;
  updatedAt: string;
}[] = [
  {
    customQuestionId: 901,
    sourceType: 'CUSTOM',
    relationType: 'COUPLE',
    matchId: 0,
    text: '상대방의 첫 인상은 어땠나요?',
    isEditable: true,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  },
];

export const customHandlers = [
  // 조회
  http.get('/api/matches/:matchId/custom-questions', ({ params, request }) => {
    const { matchId } = params;

    customQuestions = customQuestions.map((q) =>
      q.customQuestionId === 901 ? { ...q, matchId: Number(matchId) } : q,
    );

    const url = new URL(request.url);
    const page = Number(url.searchParams.get('page') ?? 0);
    const size = Number(url.searchParams.get('size') ?? 20);

    const filtered = customQuestions.filter((q) => q.matchId === Number(matchId));

    const start = page * size;
    const end = start + size;
    const paged = filtered.slice(start, end);

    return HttpResponse.json({
      content: paged,
      pageable: { pageNumber: page, pageSize: size },
      totalElements: filtered.length,
      totalPages: Math.ceil(filtered.length / size),
      empty: paged.length === 0,
    });
  }),

  // 등록
  http.post('/api/matches/:matchId/custom-questions', async ({ params, request }) => {
    const { matchId } = params;
    const body = ((await request.json()) ?? {}) as { text?: string };

    const text = typeof body.text === 'string' ? body.text.trim() : '';
    if (!text || text.length > 100) {
      return HttpResponse.json(
        { error: 'FIELD_VALIDATION_FAILED', message: 'text(1~100자)이 필요합니다.' },
        { status: 400 },
      );
    }

    const newItem = {
      customQuestionId: Math.floor(Math.random() * 10000),
      sourceType: 'CUSTOM' as const,
      relationType: 'COUPLE' as const,
      matchId: Number(matchId),
      text,
      isEditable: true,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };

    customQuestions.push(newItem);

    return HttpResponse.json(newItem, { status: 201 });
  }),

  // 수정
  http.patch('/api/custom-questions/:id', async ({ params, request }) => {
    const { id } = params;
    const body = ((await request.json()) ?? {}) as { text?: string };

    const text = typeof body.text === 'string' ? body.text.trim() : '';
    if (!text || text.length > 100) {
      return HttpResponse.json(
        { error: 'FIELD_VALIDATION_FAILED', message: 'text(1~100자)이 필요합니다.' },
        { status: 400 },
      );
    }

    const idx = customQuestions.findIndex((q) => q.customQuestionId === Number(id));
    if (idx === -1) {
      return HttpResponse.json(
        { error: 'NOT_FOUND', message: '해당 질문을 찾을 수 없습니다.' },
        { status: 404 },
      );
    }

    customQuestions[idx] = {
      ...customQuestions[idx],
      text,
      updatedAt: new Date().toISOString(),
    };

    return HttpResponse.json(customQuestions[idx], { status: 200 });
  }),

  // 삭제
  http.delete('/api/custom-questions/:id', ({ params }) => {
    const { id } = params;
    const before = customQuestions.length;

    customQuestions = customQuestions.filter((q) => q.customQuestionId !== Number(id));

    if (customQuestions.length === before) {
      return HttpResponse.json(
        { error: 'NOT_FOUND', message: '삭제할 질문이 없습니다.' },
        { status: 404 },
      );
    }

    return new HttpResponse(null, { status: 204 });
  }),
];
