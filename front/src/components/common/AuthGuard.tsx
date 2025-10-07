'use client';

import { usePathname, useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';

export default function AuthGuard({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const [isChecked, setIsChecked] = useState(false);
  const pathName = usePathname();
  const home = pathName === '/';
  const login = pathName.startsWith('/login');
  const signup = pathName.startsWith('/signup');

  useEffect(() => {
    const token = localStorage.getItem('accessToken');

    // accessToken 없으면 로그인 페이지로 이동
    if (!token && !home) {
      router.replace('/login');
      return;
    }

    // 토큰 존재하면 렌더링 허용
    setIsChecked(true);
  }, [router]);

  if (!isChecked && !home && !login && !signup) return null;
  return <>{children}</>;
}
