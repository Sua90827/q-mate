'use client';
import Image from 'next/image';
import React from 'react';
import ExpBubble from './ui/ExpBubble';
import Bubbley from './ui/Bubbley';
import { ExpBar } from './ui/ExpBar';
import ChartModal from '../charts/ChartModal';
import { useThemeStore } from '@/store/useThemeStore';
import { motion, AnimatePresence } from 'motion/react';
import { Theme } from '@/types/theme';

export default function Main() {
  const { theme, hasHydrated } = useThemeStore();

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

  const key: Theme = hasHydrated && theme in paths ? (theme as Theme) : 'day';

  const { web, mobile, light } = paths[key];

  const MotionImage = motion(Image);

  return (
    <div className="w-full min-h-screen flex flex-col items-center justify-center">
      <div className="fixed inset-0 pointer-events-none z-0">
        <AnimatePresence mode="wait">
          <MotionImage
            key={`${theme}-light`}
            src={light}
            alt="빛 효과 이미지"
            fill
            className="object-contain object-top -translate-x-5 invisible md:visible"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.8 }}
            priority
          />
          <picture>
            <source media="(max-width: 768px)" srcSet={mobile} />
            <MotionImage
              key={`${theme}-web`}
              src={web}
              alt="배경 장식 이미지"
              fill
              sizes="100vw"
              className="object-fill object-bottom"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              transition={{ duration: 0.8 }}
              priority
            />
          </picture>
        </AnimatePresence>
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
