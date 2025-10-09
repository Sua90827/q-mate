import type { NextRequest } from 'next/server';
import { NextResponse } from 'next/server';

export function middleware(req: NextRequest) {
  const now = new Date();
  const hourKST = (now.getUTCHours() + 9) % 24; // vercel 배포 시 시간 맞추기 용
  let theme: 'day' | 'sunset' | 'night' = 'day';

  if (hourKST >= 6 && hourKST < 18) theme = 'day';
  else if (hourKST >= 18 && hourKST < 20) theme = 'sunset';
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
