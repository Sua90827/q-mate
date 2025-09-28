import { http, HttpResponse, delay } from 'msw';

export const questionHandlers = [
  // 매칭별 오늘의 질문 조회 (스펙 일치: answers 배열 포함)
  http.get('/api/matches/:matchId/questions/today', async ({ params }) => {
    const { matchId } = params;
    await delay(200);

    return HttpResponse.json({
      questionInstanceId: 123,
      matchId: Number(matchId),
      deliveredAt: new Date(Date.now() - 15 * 60 * 1000).toISOString(),
      status: 'PENDING', // 'PENDING' | 'COMPLETED'
      completedAt: null,
      question: {
        questionId: 777,
        sourceType: 'ADMIN',
        relationType: 'COUPLE',
        category: { id: 3, name: '취향' },
        text: '연인이 가장 좋아하는 음식은?',
      },

      answers: [
        {
          answerId: 460,
          userId: 99,
          nickname: '내 닉네임',
          visible: true,
          content: '초밥',
          submittedAt: new Date(Date.now() - 10 * 60 * 1000).toISOString(),
          mine: true,
          isMine: true,
        },
        {
          answerId: null,
          userId: 100,
          nickname: '상대 닉네임',
          visible: false,
          content: null,
          submittedAt: null,
          mine: false,
          isMine: false,
        },
      ],
    });
  }),

  // 질문 인스턴스 상세 조회
  http.get('/api/question-instances/:questionInstanceId', async ({ params }) => {
    const { questionInstanceId } = params;
    await delay(200);

    return HttpResponse.json({
      questionInstanceId: Number(questionInstanceId),
      matchId: 10,
      deliveredAt: new Date(Date.now() - 60 * 60 * 1000).toDateString,
      status: 'PENDING',
      completedAt: null,
      question: {
        questionId: 778,
        sourceType: 'ADMIN',
        relationType: 'COUPLE',
        category: { id: 5, name: '일상' },
        text: '주말에 같이 하고 싶은 일은?',
      },
      answers: [
        {
          answerId: 461,
          userId: 99,
          nickname: '내 닉네임',
          isMine: true,
          visible: true,
          content: '영화 보기',
          submittedAt: new Date(Date.now() - 50 * 60 * 1000).toISOString(),
        },
        {
          answerId: null,
          userId: 100,
          nickname: '상대 닉네임',
          isMine: false,
          visible: false,
          content: null,
          submittedAt: null,
        },
      ],
    });
  }),

  // 질문 인스턴스에 대한 답변 등록 (유효성: 문자열, 1~100자)
  http.post('/api/question-instances/:questionInstanceId/answers', async ({ params, request }) => {
    const { questionInstanceId } = params;
    const body = (await request.json()) as { content?: string };

    const content = typeof body?.content === 'string' ? body.content.trim() : '';
    if (!content || content.length > 100) {
      return HttpResponse.json(
        { error: 'FIELD_VALIDATION_FAILED', message: 'content(1~100자)이 필요합니다.' },
        { status: 400 },
      );
    }

    await delay(200);
    const now = new Date().toISOString();
    return HttpResponse.json(
      {
        answerId: Math.floor(Math.random() * 10000),
        questionInstanceId: Number(questionInstanceId),
        userId: 99,
        nickname: '내 닉네임',
        visible: true,
        content,
        submittedAt: now,
        updatedAt: now,
        mine: true,
        isMine: true,
      },
      { status: 201 },
    );
  }),

  // 답변 수정 (내용 교체, 유효성: 문자열, 1~100자)
  http.patch('/api/answers/:answerId', async ({ params, request }) => {
    const { answerId } = params;
    const body = (await request.json()) as { content?: string };

    const content = typeof body?.content === 'string' ? body.content.trim() : '';
    if (!content || content.length > 100) {
      return HttpResponse.json(
        { error: 'FIELD_VALIDATION_FAILED', message: 'content(1~100자)이 필요합니다.' },
        { status: 400 },
      );
    }

    await delay(200);
    return HttpResponse.json({
      answerId: Number(answerId),
      questionInstanceId: 123,
      userId: 99,
      nickname: '내 닉네임',
      visible: true,
      content,
      submittedAt: new Date(Date.now() - 30 * 60 * 1000).toISOString(),
      updatedAt: new Date().toISOString(),
      mine: true,
      isMine: true,
    });
  }),

  // 질문 평가 등록
  http.post('/api/questions/:questionId/ratings', async ({ params, request }) => {
    const { questionId } = params;
    const body = (await request.json()) as { isLike?: boolean };

    if (typeof body?.isLike !== 'boolean') {
      return HttpResponse.json(
        { error: 'FIELD_VALIDATION_FAILED', message: 'isLike(boolean) is required' },
        { status: 400 },
      );
    }

    await delay(200);
    return HttpResponse.json(
      {
        ratingId: Math.floor(Math.random() * 10000),
        questionId: Number(questionId),
        userId: 99,
        isLike: body.isLike,
        createdAt: new Date().toISOString(),
      },
      { status: 201 },
    );
  }),
];
