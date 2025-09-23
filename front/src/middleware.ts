import type { NextRequest } from 'next/server';
import { NextResponse } from 'next/server';

export function middleware(req: NextRequest) {
  const hour = new Date().getHours();
  let theme: 'day' | 'sunset' | 'night' = 'day';

  if (hour >= 6 && hour < 18) theme = 'day';
  else if (hour >= 18 && hour < 20) theme = 'sunset';
  else theme = 'night';

  const res = NextResponse.next();

  // 쿠키 저장
  res.cookies.set('theme', theme, {
    path: '/', // 모든 경로에서 유효
  });

  return res;
}

export const config = {
  matcher: ['/:path*'], // 모든 라우트에서 실행
};
