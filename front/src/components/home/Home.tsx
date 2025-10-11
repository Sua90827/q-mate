'use client';
import Image from 'next/image';
import { Button } from '../common/Button';
import { useAuthStore } from '@/store/useAuthStore';
import { useRouter } from 'next/navigation';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { useSelectedStore } from '@/store/useSelectedStore';

export default function Home() {
  const router = useRouter();

  const accessToken = useAuthStore((state) => state.accessToken);
  const resetAccessToken = useAuthStore((state) => state.resetAccessToken);
  const resetMatchId = useMatchIdStore((state) => state.resetMatchId);
  const { setSelectedMenu, resetSelectedMenu } = useSelectedStore();
  const matchId = useMatchIdStore((state) => state.matchId);

  const handleLogout = () => {
    resetAccessToken();
    resetMatchId();
    resetSelectedMenu();
    localStorage.removeItem('accessToken');
    localStorage.clear();
    router.replace('/login');
  };

  const checkLogin = () => {
    const savedTime = Number(localStorage.getItem('accessTokenTime'));

    // 토큰이나 만료 정보가 없으면 로그아웃
    if (!accessToken || !savedTime) {
      handleLogout();
      return;
    }

    // 만료된 토큰이면 로그아웃
    if (Date.now() >= savedTime) {
      handleLogout();
      return;
    }

    // 매칭은 아직 안 되어 있을 때
    if (accessToken && !matchId) {
      router.replace('/invite');
      return;
    }

    // 정상 로그인 상태 → 홈으로
    setSelectedMenu('home');
    router.replace('/main');
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
