import { http, HttpResponse } from 'msw';

export const petHandlers = [
  http.get('/api/matches/:matchId/pet', () => {
    return HttpResponse.json({ exp: 100 });
  }),
];
