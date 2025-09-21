'use client';
import Image from 'next/image';
import React from 'react';
import ExpBubble from './ui/ExpBubble';
import Bubbley from './ui/Bubbley';
import { ExpBar } from './ui/ExpBar';
import { useThemeByTime } from '@/hooks/useThemeByTime';
import ChartModal from '../charts/ChartModal';

export default function Main() {
  const theme = useThemeByTime();

  const paths = {
    day: {
      web: '/images/day/day_deco_W.png',
      mobile: '/images/day/day_deco_M.png',
      light: '/images/day/day_light.png',
    },
    sunset: {
      web: '/images/sunset/sunset_deco_W.png',
      mobile: '/images/sunset/sunset_deco_M.png',
      light: '/images/sunset/sunset_light.png',
    },
    night: {
      web: '/images/night/night_deco_W.png',
      mobile: '/images/night/night_deco_M.png',
      light: '/images/night/night_light.png',
    },
  };

  const web = paths[theme].web;
  const mobile = paths[theme].mobile;
  const light = paths[theme].light;

  return (
    <div className="w-full min-h-screen flex flex-col items-center justify-center">
      <div className="fixed inset-0 pointer-events-none z-0">
        <Image
          src={light}
          alt="빛 효과 이미지"
          fill
          className="object-contain object-top -translate-x-5 invisible md:visible"
          priority
        />

        <picture>
          <source media="(max-width: 768px)" srcSet={mobile} />
          <Image
            src={web}
            alt="배경 장식 이미지"
            priority
            fill
            sizes="100vw"
            className="object-fill object-bottom"
          />
        </picture>
      </div>

      <div className="relative z-10 flex flex-col items-center justify-center w-[252px] h-[358px] mt-15">
        <ExpBubble />
        <Bubbley exp={0} className="mb-6" />
        <ExpBar />
      </div>
      <ChartModal />
    </div>
  );
}
