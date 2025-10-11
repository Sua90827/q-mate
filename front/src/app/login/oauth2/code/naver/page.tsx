'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/store/useAuthStore';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { useSelectedStore } from '@/store/useSelectedStore';
import { fetchPetInfo } from '@/api/pet';
import { ErrorToast } from '@/components/common/CustomToast';
import Loader from '@/components/common/Loader';
import { verifyState } from '@/utils/naveState';

export default function NaverCallbackPage() {
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();
  const setAccessToken = useAuthStore((s) => s.setAccessToken);
  const setMatchId = useMatchIdStore((s) => s.setMatchId);
  const setSelectedMenu = useSelectedStore((s) => s.setSelectedMenu);

  useEffect(() => {
    (async () => {
      try {
        const params = new URLSearchParams(window.location.search);
        const code = params.get('code');
        const state = params.get('state');

        if (!verifyState('naver', state)) throw new Error('잘못된 요청입니다. (state 검증 실패)');
        if (!code) throw new Error('code가 없습니다.');

        const res = await fetch('/auth/naver/exchange', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify({
            code,
            state,
            redirectUri: process.env.NEXT_PUBLIC_NAVER_REDIRECT_URI,
          }),
        });

        if (!res.ok) {
          const msg = await res.text();
          throw new Error(msg || '네이버 교환 실패');
        }

        const data = await res.json();

        const { accessToken, accessTokenExpiresIn, user } = data;

        if (!accessToken || !user) throw new Error('잘못된 응답입니다.');

        setAccessToken(accessToken);
        localStorage.setItem('accessTokenTime', String(Date.now() + accessTokenExpiresIn * 1000));

        if (user.currentMatchId) {
          setMatchId(user.currentMatchId);
          const petInfo = await fetchPetInfo(user.currentMatchId);
          localStorage.setItem('prevExp', String(petInfo.exp));
          setSelectedMenu('home');
          router.replace('/main');
        } else if (!user.currentMatchId && accessToken) {
          router.replace('/invite');
        } else {
          if (user.nickname) sessionStorage.setItem('nickname', user.nickname);
          if (user.birthDate) sessionStorage.setItem('birthDate', user.birthDate);
          router.replace('/signup/onboarding');
        }
      } catch (e) {
        const err = e instanceof Error ? e : new Error(String(e));
        setError(err.message);
      }
    })();
  }, []);

  if (error) return ErrorToast(error);
  return <Loader />;
}
