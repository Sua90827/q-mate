'use client';
import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import axios from 'axios';
import { useAuthStore } from '@/store/useAuthStore';
import { useSelectedStore } from '@/store/useSelectedStore';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { fetchPetInfo } from '@/api/pet';
import Loader from '../common/Loader';

export default function LoginSuccess() {
  const router = useRouter();
  const setMatchId = useMatchIdStore((state) => state.setMatchId);
  const setAccessToken = useAuthStore((state) => state.setAccessToken);
  const setSelectedMenu = useSelectedStore((state) => state.setSelectedMenu);

  useEffect(() => {
    const checkLogin = async () => {
      try {
        const res = await axios.post('/auth/exchange', {}, { withCredentials: true });
        const data = res.data;

        // 응답 유효성 먼저 확인
        if (!data || !data.user || !data.accessToken) {
          throw new Error('유효하지 않은 로그인 응답');
        }

        if (data.user.currentMatchId) {
          // 매칭된 유저 → 메인으로
          setMatchId(data.user.currentMatchId);

          const petInfo = await fetchPetInfo(data.user.currentMatchId);
          localStorage.setItem('prevExp', String(petInfo.exp));

          setAccessToken(data.accessToken);
          const accessTokenTime = Date.now() + data.accessTokenExpiresIn * 1000;
          localStorage.setItem('accessTokenTime', String(accessTokenTime));
          setSelectedMenu('home');
          router.replace('/main');
        } else {
          // 매칭 안 된 유저 → 온보딩으로
          setAccessToken(data.accessToken);
          if (data.user.nickname) sessionStorage.setItem('nickname', data.user.nickname);
          if (data.user.birthDate) sessionStorage.setItem('birthDate', data.user.birthDate);
          router.replace('/signup/onboarding');
        }
      } catch (err) {
        router.replace('/login');
      }
    };

    checkLogin();
  }, [router, setAccessToken, setMatchId, setSelectedMenu]);

  return <Loader />;
}
