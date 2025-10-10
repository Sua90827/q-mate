'use client';
import Image from 'next/image';
import { Button } from '../common/Button';
import { useAuthStore } from '@/store/useAuthStore';
import { useRouter } from 'next/navigation';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { useSelectedStore } from '@/store/useSelectedStore';
import { useEffect, useState } from 'react';
import instance from '@/api/axiosInstance';

export default function Home() {
  const router = useRouter();
  const accessToken = useAuthStore((state) => state.accessToken);
  const resetMatchId = useMatchIdStore((state) => state.resetMatchId);
  const resetAccessToken = useAuthStore((state) => state.resetAccessToken);
  const setSelectedMenu = useSelectedStore((state) => state.setSelectedMenu);
  const resetSelectedMenu = useSelectedStore((state) => state.resetSelectedMenu);

  const [accessTokenTime, setAccessTokenTime] = useState<number | null>(null);

  useEffect(() => {
    const savedTime = localStorage.getItem('accessTokenTime');
    if (savedTime) setAccessTokenTime(Number(savedTime));
  }, []);

  const checkLogin = () => {
    if (accessToken && accessTokenTime && Date.now() < accessTokenTime) {
      setSelectedMenu('home');
      router.replace('/main');
    } else {
      resetSelectedMenu();
      localStorage.clear();
      resetMatchId();
      resetAccessToken();
      instance.defaults.headers.Authorization = '';
      router.replace('/login');
    }
  };

  return (
    <div className="w-full h-full flex flex-col items-center justify-center pt-[70px] sm:pt-[0px] sm:pb-[70px]">
      <div className="absolute inset-0 pointer-events-none z-0">
        <picture>
          <source media="(max-width: 768px)" srcSet="/images/background_deco_M.png" />
          <Image
            src="/images/background_deco_W.png"
            alt="배경 장식 이미지"
            priority
            fill
            sizes="100vw"
            className="object-cover object-bottom"
          />
        </picture>
      </div>

      <div className="flex flex-col gap-5 items-center justify-center">
        <Image src="/images/logo/day_logo.svg" alt="큐메이트" width={173} height={55} />
        <p className="font-Gumi text-24 cursor-none">함께 하루를 기록해봐요!</p>
        <Button variant="invite" className="w-[300px] mt-6 cursor-pointer" onClick={checkLogin}>
          시작 하기
        </Button>
      </div>
    </div>
  );
}
