'use client';
import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { ErrorToast } from '@/components/common/CustomToast';
import Loader from '@/components/common/Loader';
import { useAuthStore } from '@/store/useAuthStore';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { useSelectedStore } from '@/store/useSelectedStore';
import { fetchPetInfo } from '@/api/pet';
import { socialAxios } from '@/lib/socialAxios';

export default function GoogleCallback() {
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();

  const setAccessToken = useAuthStore((s) => s.setAccessToken);
  const setMatchId = useMatchIdStore((s) => s.setMatchId);
  const setSelectedMenu = useSelectedStore((s) => s.setSelectedMenu);

  useEffect(() => {
    const url = new URL(window.location.href);
    const code = url.searchParams.get('code');
    if (!code) {
      setError('Google code 누락');
      return;
    }

    exchangeCode(code);
  }, []);

  const exchangeCode = async (code: string) => {
    try {
      const res = await socialAxios.post('/google/exchange', {
        code,
        redirectUri: process.env.NEXT_PUBLIC_GOOGLE_REDIRECT_URI,
      });

      const { accessToken, accessTokenExpiresIn, user } = res.data;
      if (!accessToken || !user) throw new Error('잘못된 응답');

      // 액세스 토큰 저장
      setAccessToken(accessToken);
      localStorage.setItem('accessTokenTime', String(Date.now() + accessTokenExpiresIn * 1000));

      // 매칭 여부 분기
      if (user.currentMatchId) {
        setMatchId(user.currentMatchId);
        const petInfo = await fetchPetInfo(user.currentMatchId);
        localStorage.setItem('prevExp', String(petInfo.exp));
        setSelectedMenu('home');
        router.replace('/main');
      } else {
        if (user.nickname) sessionStorage.setItem('nickname', user.nickname);
        if (user.birthDate) sessionStorage.setItem('birthDate', user.birthDate);
        router.replace('/signup/onboarding');
      }
    } catch (e) {
      const err = e as Error;
      setError(`교환 실패: ${err.message}`);
    }
  };

  if (error) return ErrorToast(error);
  return <Loader />;
}
