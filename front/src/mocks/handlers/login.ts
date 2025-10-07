import { http, HttpResponse, delay } from 'msw';

type MockUser = {
  userId: number;
  email: string;
  password: string;
  nickname: string;
  birthDate: string;
  role: 'USER' | 'ADMIN' | string;
  currentMatchId: number | null;
  pushEnabled: boolean | null;
};

const mockUsers: MockUser[] = [
  {
    userId: 1,
    email: 'test@naver.com',
    password: 'test1234!',
    nickname: '테스트유저',
    birthDate: '1998-05-10',
    role: 'USER',
    currentMatchId: 1,
    pushEnabled: null,
  },
];

export const authHandlers = [
  http.post('/auth/login', async ({ request }) => {
    await delay(200);
    const { email, password } = (await request.json()) as { email?: string; password?: string };

    const user = mockUsers.find((u) => u.email === email && u.password === password);
    if (!user) {
      return HttpResponse.json({ message: 'Invalid email or password' }, { status: 401 });
    }

    return HttpResponse.json({
      accessToken: 'mock-access-token',
      refreshToken: 'mock-refresh-token',
      tokenType: 'Bearer',
      accessTokenExpiresIn: 3600,
      refreshTokenExpiresIn: 86400,
      user: {
        userId: user.userId,
        email: user.email,
        nickname: user.nickname,
        birthDate: user.birthDate,
        role: user.role,
        currentMatchId: user.currentMatchId,
        pushEnabled: user.pushEnabled,
      },
    });
  }),
];
