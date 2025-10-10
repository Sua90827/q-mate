'use client';
import { useEffect } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';
import axios from 'axios';
import { useAuthStore } from '@/store/useAuthStore';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { useSelectedStore } from '@/store/useSelectedStore';
import { fetchPetInfo } from '@/api/pet';
import Loader from '@/components/common/Loader';

export default function GoogleCallback() {
  const router = useRouter();
  const searchParams = useSearchParams();

  const setAccessToken = useAuthStore((state) => state.setAccessToken);
  const setMatchId = useMatchIdStore((state) => state.setMatchId);
  const setSelectedMenu = useSelectedStore((state) => state.setSelectedMenu);

  useEffect(() => {
    const code = searchParams.get('code');
    if (!code) {
      router.replace('/login');
      return;
    }

    const exchangeCode = async () => {
      try {
        const res = await axios.post(
          `${process.env.NEXT_PUBLIC_BACKEND_ORIGIN}/auth/exchange`,
          { code },
          { withCredentials: true },
        );

        const data = res.data;
        if (!data || !data.accessToken || !data.user) throw new Error('잘못된 응답');

        // 로그인 후 분기 처리
        if (data.user.currentMatchId) {
          setMatchId(data.user.currentMatchId);
          const petInfo = await fetchPetInfo(data.user.currentMatchId);
          localStorage.setItem('prevExp', String(petInfo.exp));
          setAccessToken(data.accessToken);
          localStorage.setItem(
            'accessTokenTime',
            String(Date.now() + data.accessTokenExpiresIn * 1000),
          );
          setSelectedMenu('home');
          router.replace('/main');
        } else {
          setAccessToken(data.accessToken);
          if (data.user.nickname) sessionStorage.setItem('nickname', data.user.nickname);
          if (data.user.birthDate) sessionStorage.setItem('birthDate', data.user.birthDate);
          router.replace('/signup/onboarding');
        }
      } catch (err) {
        router.replace('/login');
      }
    };

    exchangeCode();
  }, [searchParams, router, setAccessToken, setMatchId, setSelectedMenu]);

  return <Loader />;
}
