'use client';

import React from 'react';
import Image from 'next/image';
//TODO: API 연동 후 경험치 값 props로 받기
// 사용자가 main에 접근하고 exp가 오를때 ExpBubble 컴포넌트가 나타나도록 구현
type ExpBubbleProps = {
  exp: number;
};
export default function ExpBubble() {
  return (
    <div className="relative w-[132px] h-[132px]">
      <Image
        src="/images/bubble.png"
        alt="경험치 버블"
        fill
        className="rounded-full object-contain"
        sizes="100%"
      />
      <div className="absolute inset-0 flex flex-col items-center justify-center text-center gap-1">
        <span className="font-jalnan text-[24px] font-regular">EXP</span>
        <span className="font-jalnan text-[14px] font-regular ">+ 10</span>
      </div>
    </div>
  );
}
