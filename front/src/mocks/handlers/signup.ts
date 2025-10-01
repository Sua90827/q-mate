import { http, HttpResponse } from 'msw';

// 회원가입 시 이메일 인증코드
export const signupHandlers = [
  http.post('/auth/email-verifications', async ({ request }) => {
    const body = (await request.json()) as { email: string; purpose: string };
    console.log('Mock POST /auth/email-verifications', body);
    return HttpResponse.json({ sent: true });
  }),

  //이메일 재전송
  http.post('/auth/email-verifications/resend', async ({ request }) => {
    const body = (await request.json()) as { email: string; purpose: string };
    console.log('Mock POST /auth/email-verifications/resend', body);
    return HttpResponse.json({ resent: true });
  }),

  //이메일 인증코드 확인
  http.post('/auth/email-verifications/verify', async ({ request }) => {
    const body = (await request.json()) as { email: string; code: string; purpose: string };
    console.log('Mock POST /auth/email-verifications/verify', body);
    return HttpResponse.json({ verified: true, email_verified_token: 'mock-email-verified-token' });
  }),

  //자체 회원가입
  http.post('/auth/register', async ({ request }) => {
    const body = (await request.json()) as {
      email: string;
      password: string;
      nickname: string;
      birthDate: string;
      emailVerifiedToken: string;
    };
    console.log('Mock POST /auth/register', body);
    return HttpResponse.json({ registered: true });
  }),
];
