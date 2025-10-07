'use client';

import { useAuthStore } from '@/store/useAuthStore';
import { usePathname, useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';

export default function AuthGuard({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const pathName = usePathname();
  const [isChecked, setIsChecked] = useState(false);
  const accessToken = useAuthStore((state) => state.accessToken);

  const home = pathName === '/';
  const login = pathName.startsWith('/login');
  const signup = pathName.startsWith('/signup');

  useEffect(() => {
    // accessToken 없으면 로그인 페이지로 이동
    if (!accessToken && !home && !login && !signup) {
      router.replace('/login');
      return;
    }
    // 토큰 존재하면 렌더링 허용
    setIsChecked(true);
  }, [accessToken, pathName, router, home, login, signup]);

  if (!isChecked) return null;

  return <>{children}</>;
}
