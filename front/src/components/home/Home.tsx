'use client';
import Image from 'next/image';
import Link from 'next/link';
import { Button } from '../common/Button';

export default function Home() {
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
        <p className="font-Gumi text-24">함께 하루를 기록해봐요!</p>
        <Button variant="invite" className="w-[300px] mt-6" asChild>
          <Link href="/login">시작 하기</Link>
        </Button>
      </div>
    </div>
  );
}
