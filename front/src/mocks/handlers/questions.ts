import { http, HttpResponse, delay } from 'msw';

type Answer = {
  answerId: number | null;
  userId: number;
  nickname: string;
  isMine: boolean;
  visible: boolean;
  content: string | null;
  submittedAt: string | null;
};

type Question = {
  questionId: number;
  sourceType: 'ADMIN' | 'CUSTOM';
  relationType: 'COUPLE';
  category: { id: number; name: string };
  text: string;
};

type QuestionDetail = {
  questionInstanceId: number;
  matchId: number;
  deliveredAt: string;
  status: 'PENDING' | 'COMPLETED';
  completedAt: string | null;
  question: Question;
  answers: Answer[];
};

export const questionHandlers = [
  //전체 질문 리스트 조회
  http.get('/api/matches/:matchId/question-instances', async ({ params, request }) => {
    const { matchId } = params;
    await delay(200);

    const url = new URL(request.url);
    const page = Number(url.searchParams.get('page') ?? 0);
    const size = Number(url.searchParams.get('size') ?? 20);

    const allInstances = [
      {
        questionInstanceId: 10,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '주말에 같이 하고 싶은 일은?',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 11,
        deliveredAt: new Date().toISOString(),
        status: 'PENDING',
        text: '가장 기억에 남는 여행은?',
        completedAt: null,
      },
      {
        questionInstanceId: 12,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '처음 만난 날 기분은 어땠나요?',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 13,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '4',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 14,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '5',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 15,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '6',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 16,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '7?',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 17,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '8?',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 18,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '9?',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 19,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '10',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 20,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '11?',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 21,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '12?',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 22,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '13?',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 23,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '14?',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 24,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '15?',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 25,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '16',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 26,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '17',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 27,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '18',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 28,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '19',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 29,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '20',
        completedAt: new Date().toISOString(),
      },
      {
        questionInstanceId: 30,
        deliveredAt: new Date().toISOString(),
        status: 'COMPLETED',
        text: '21',
        completedAt: new Date().toISOString(),
      },
    ];

    const start = page * size;
    const end = start + size;
    const paged = allInstances.slice(start, end);

    return HttpResponse.json({
      content: paged,
      pageable: {
        pageNumber: page,
        pageSize: size,
        offset: start,
        paged: true,
        unpaged: false,
      },
      totalPages: Math.ceil(allInstances.length / size),
      totalElements: allInstances.length,
      last: page + 1 >= Math.ceil(allInstances.length / size),
      size,
      number: page,
      numberOfElements: paged.length,
      first: page === 0,
      empty: paged.length === 0,
    });
  }),

  // 질문 인스턴스 상세 조회 (단일 질문)
  http.get('/api/question-instances/:questionInstanceId', async ({ params }) => {
    const { questionInstanceId } = params;
    await delay(200);

    const id = Number(questionInstanceId);

    // id별 mock 데이터
    const mockDetails: Record<number, QuestionDetail> = {
      10: {
        questionInstanceId: 10,
        matchId: 1,
        deliveredAt: new Date(Date.now() - 60 * 60 * 1000).toISOString(),
        status: 'PENDING',
        completedAt: new Date().toISOString(),
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
            nickname: '조용한 유령',
            isMine: true,
            visible: true,
            content: '영화 보기',
            submittedAt: new Date(Date.now() - 50 * 60 * 1000).toISOString(),
          },
          {
            answerId: null,
            userId: 100,
            nickname: '활기찬 고래',
            isMine: false,
            visible: false,
            content: null,
            submittedAt: null,
          },
        ],
      },
      11: {
        questionInstanceId: 11,
        matchId: 1,
        deliveredAt: new Date().toISOString(),
        status: 'PENDING',
        completedAt: null,
        question: {
          questionId: 779,
          sourceType: 'ADMIN',
          relationType: 'COUPLE',
          category: { id: 6, name: '여행' },
          text: '가장 기억에 남는 여행은?',
        },
        answers: [], // 아직 답변 없음
      },
    };

    // id에 해당하는 데이터가 없으면 null 반환
    if (!mockDetails[id]) {
      return HttpResponse.json(null, { status: 200 });
    }

    return HttpResponse.json(mockDetails[id]);
  }),

  // 매칭별 오늘의 질문 조회 (스펙 일치: answers 배열 포함)
  http.get('/api/matches/:matchId/questions/today', async ({ params }) => {
    const { matchId } = params;
    await delay(200);

    return HttpResponse.json({
      questionInstanceId: 19,
      matchId: Number(matchId),
      deliveredAt: new Date(Date.now() - 15 * 60 * 1000).toISOString(),
      status: 'PENDING', // 'PENDING' | 'COMPLETED'
      completedAt: null,
      question: {
        questionId: 778,
        sourceType: 'ADMIN',
        relationType: 'COUPLE',
        category: { id: 3, name: '취향' },
        text: '연인이 가장 좋아하는 음식은?',
      },

      answers: [
        {
          answerId: 460,
          userId: 1,
          nickname: '내 닉네임',
          visible: true,
          content: '초밥',
          submittedAt: new Date(Date.now() - 10 * 60 * 1000).toISOString(),
          mine: false,
          isMine: false,
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

  // 질문 인스턴스에 대한 답변 등록 (유효성: 문자열, 1~100자)
  http.post('/api/question-instances/:questionInstanceId/answers', async ({ params, request }) => {
    const { questionInstanceId } = params;
    const body = (await request.json()) as { content?: string };

    const content = body?.content ?? '';
    if (content.length < 1 || content.length > 100) {
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
        content,
        submittedAt: now,
        updatedAt: now,
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
