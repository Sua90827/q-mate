import Image from 'next/image';
import React, { ReactNode } from 'react';

export default function InviteLayout({ children }: { children: ReactNode }) {
  return (
    <div className="w-full h-full flex flex-col items-center justify-center pb-[70px]">
      <div className="absolute inset-0 pointer-events-none z-0">
        <picture>
          <source media="(max-width: 768px) " srcSet="/images/background_deco_M.png" />

          <Image
            src="/images/background_deco_W.png"
            alt="배경 장식 이미지"
            priority
            fill
            sizes="100vw"
            className="object-cover object-bottom "
          />
        </picture>
      </div>
      {children}
    </div>
  );
}
