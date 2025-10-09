'use client';
import Image from 'next/image';
import { Button } from '../common/Button';
import { useAuthStore } from '@/store/useAuthStore';
import { useRouter } from 'next/navigation';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { useSelectedStore } from '@/store/useSelectedStore';

export default function Home() {
  const accessToken = useAuthStore((state) => state.accessToken);
  const accessTokenTime = Number(localStorage.getItem('accessTokenTime'));
  const router = useRouter();
  const resetMatchId = useMatchIdStore((state) => state.resetMatchId);
  const resetAccessToken = useAuthStore((state) => state.resetAccessToken);
  const setSelectedMenu = useSelectedStore((state) => state.setSelectedMenu);
  const checkLogin = () => {
    if (accessToken && Date.now() < accessTokenTime) {
      // 아직 유효하면 메인으로 이동
      setSelectedMenu('home');
      router.replace('/main');
    } else if ((accessToken && Date.now() >= accessTokenTime) || !accessTokenTime) {
      // 만료된 토큰이면 전부 정리
      //선택된 메뉴 리셋
      setSelectedMenu('home');
      // exp 리셋
      localStorage.clear();
      // 매치 아이디 리셋
      resetMatchId();
      // 토큰 리셋
      resetAccessToken();
      router.replace('/login');
    }
  };

  return (
    <div className="w-full h-full  flex flex-col items-center justify-center pb-[70px]">
      <div className="absolute inset-0 pointer-events-none z-0">
        <picture>
          <source media="(max-width: 768px) " srcSet="/images/background_deco_M.png" />

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
