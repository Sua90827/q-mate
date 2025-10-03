import { http, HttpResponse, delay } from 'msw';

export const chartHandlers = [
  http.get('/api/matches/:matchId/stats/likes-by-category/monthly', async ({ params }) => {
    const { matchId } = params;
    await delay(300);

    return HttpResponse.json({
      matchId: Number(matchId),
      anchorDate: new Date().toISOString(),
      categories: [
        { categoryId: 1, categoryName: '취미', likeCount: 25 },
        { categoryId: 2, categoryName: '선호도', likeCount: 18 },
        { categoryId: 3, categoryName: '추억', likeCount: 15 },
        { categoryId: 4, categoryName: '미래 계획', likeCount: 14 },
        { categoryId: 5, categoryName: '상황 가정', likeCount: 8 },
        { categoryId: 6, categoryName: '기념일(100일)', likeCount: 20 },
        { categoryId: 7, categoryName: '기념일(N주년)', likeCount: 10 },
      ],
    });
  }),
];
