'use client';
import Image from 'next/image';
import React from 'react';
import { motion } from 'motion/react';

export default function MainLoading() {
  return (
    <div className="fixed inset-0 z-50 flex flex-col items-center justify-center bg-gradient-main">
      <picture className="absolute inset-0 -z-10">
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

      <p className="font-Gumi text-24 pb-10 text-center">
        오늘도 함께
        <br />
        하루를 기록해봐요!
      </p>
      <Image
        src="/images/bubbley/bubbley_baby.png"
        alt="버블리 캐릭터"
        width={120}
        height={167}
        className="pb-10"
      />
      <div className="border-secondary border-2 rounded-xl w-[300px] h-7 overflow-hidden">
        <motion.div
          className="bg-secondary h-7 rounded-r-xl"
          initial={{ width: '0%' }}
          animate={{ width: ['0%', '100%'] }}
          transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
        />
      </div>
    </div>
  );
}
