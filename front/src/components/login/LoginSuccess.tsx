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
        const res = await axios.get('/auth/exchange', { withCredentials: true });
        const data = res.data;
        if (data?.user?.currentMatchId) {
          setMatchId(data.user.currentMatchId);
          // 서버에서 현재 exp 조회
          const petInfo = await fetchPetInfo(data.user.currentMatchId);
          // 현재 exp 셋팅
          localStorage.setItem('prevExp', String(petInfo.exp));
          // accessToken 셋팅
          setAccessToken(data.accessToken);
          const accessTokenTime = Date.now() + data.accessTokenExpiresIn * 1000;
          localStorage.setItem('accessTokenTime', String(accessTokenTime));
          setSelectedMenu('home');
          router.replace('/main');
        } else {
          if (data.accessToken) setAccessToken(data.accessToken);
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
