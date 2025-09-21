'use client';

import React from 'react';
import Image from 'next/image';
import { useThemeStore } from '@/store/useThemeStore';
//TODO: API 연동 후 경험치 값 props로 받기
// 사용자가 main에 접근하고 exp가 오를때 ExpBubble 컴포넌트가 나타나도록 구현
type ExpBubbleProps = {
  exp: number;
};
export default function ExpBubble() {
  const theme = useThemeStore((state) => state.theme);

  return (
    <div className="relative w-[132px] h-[132px]">
      <Image
        src="/images/bubble.png"
        alt="경험치 버블"
        fill
        className="rounded-full object-contain"
        sizes="100%"
      />
      <div
        className={`absolute inset-0 flex flex-col items-center justify-center text-center gap-1 ${
          theme === 'night' ? 'text-secondary' : ''
        }`}
      >
        <span className="font-Gumi text-24 font-regular">EXP</span>
        <span className="font-Gumi text-14 font-regular ">+ 10</span>
      </div>
    </div>
  );
}
