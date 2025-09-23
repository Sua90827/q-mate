'use client';
import React from 'react';
import ExpBubble from './ui/ExpBubble';
import Bubbley from './ui/Bubbley';
import { ExpBar } from './ui/ExpBar';
import ChartModal from '../charts/ChartModal';
import { motion, AnimatePresence } from 'motion/react';

export default function Main() {
  const MotionDiv = motion.div;

  return (
    <div className="w-full h-full flex flex-col items-center justify-center">
      <div className="fixed inset-0 pointer-events-none z-0">
        <AnimatePresence mode="wait">
          {/* 라이트 효과 (데스크탑 전용) */}
          <MotionDiv
            key="deco-light"
            className="hidden md:block absolute inset-0 bg-deco-light"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.8 }}
          />

          {/* 배경 장식: 모바일 / 웹 */}
          <MotionDiv
            key="deco"
            className="absolute inset-0 bg-deco"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.8 }}
          />
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
