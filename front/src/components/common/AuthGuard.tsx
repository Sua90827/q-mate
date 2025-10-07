'use client';

import { useAuthStore } from '@/store/useAuthStore';
import { usePathname, useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';

export default function AuthGuard({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const pathName = usePathname();
  const [hydrated, setHydrated] = useState(false);
  const [ready, setReady] = useState(false);

  const home = pathName === '/';
  const login = pathName.startsWith('/login');
  const signup = pathName.startsWith('/signup');

  useEffect(() => {
    //새로고침시 토큰 복원이 느려서 복원 체크
    const unsub = useAuthStore.persist.onFinishHydration(() => {
      setHydrated(true);
    });

    // 혹시 persist 이벤트가 누락될 경우를 대비한 fallback
    const timeout = setTimeout(() => setHydrated(true), 300);
    return () => {
      clearTimeout(timeout);
      unsub();
    };
  }, []);

  useEffect(() => {
    if (!hydrated) return;

    const token = useAuthStore.getState().accessToken;

    if (home || login || signup) {
      setReady(true);
      return;
    }

    if (!token) {
      router.replace('/login');
      return;
    }

    setReady(true);
  }, [hydrated, pathName, router, home, login, signup]);

  if (!ready) return null;

  return <>{children}</>;
}
