import Bubbley from '@/components/main/Bubbley';
import { ExpBar } from '@/components/main/ExpBar';
import ExpBubble from '@/components/main/ExpBubble';
import Image from 'next/image';
import React from 'react';

const page = () => {
  return (
    <div className="w-full min-h-screen flex flex-col items-center justify-center bg-gradient-main">
      <div className="absolute inset-0 pointer-events-none z-0">
        <Image
          src="/images/light.png"
          alt="빛 효과 이미지"
          fill
          className="object-contain object-top -translate-x-5"
          priority
        />
        <Image
          src="/images/deco.png"
          alt="배경 장식 이미지"
          priority
          fill
          sizes="100vw"
          className="object-fill object-bottom"
        />
      </div>

      <div className="relative z-1 flex flex-col items-center justify-center w-[252px] h-[358px]">
        <ExpBubble />
        {/** TODO: API 연동 후 경험치 값 props로 받기 */}
        <Bubbley exp={0} className="mb-6" />
        <ExpBar />
      </div>
    </div>
  );
};

export default page;
