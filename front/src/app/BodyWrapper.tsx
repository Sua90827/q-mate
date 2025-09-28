'use client';

import { usePathname } from 'next/navigation';

export default function BodyWrapper({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();

  // 기본은 테마 기반 그라데이션
  let bgClass = 'bg-gradient-theme';

  // 로그인/회원가입은 고정 배경
  if (pathname.startsWith('/login') || pathname.startsWith('/signup')) {
    bgClass = 'bg-bg-auth';
  }
  // 초대 페이지는 무조건 day 테마 배경
  else if (pathname.startsWith('/invite') || pathname === '/') {
    bgClass = 'bg-gradient-main';
  }

  return <div className={`flex flex-col w-full h-full ${bgClass}`}>{children}</div>;
}
