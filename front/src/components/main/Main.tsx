'use client';

import React, { useEffect } from 'react';
import ExpBubble from './ui/ExpBubble';
import Bubbley from './ui/Bubbley';
import { ExpBar } from './ui/ExpBar';
import ChartModal from '../charts/ChartModal';
import { motion, AnimatePresence } from 'motion/react';
import { useFetchPetInfo } from '@/hooks/usePet';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { usePetStateStore } from '@/store/usePetStore';
import BellBtn from '../common/BellBtn';

export default function Main() {
  const MotionDiv = motion.div;
  const matchId = useMatchIdStore((s) => s.matchId);
  const { currentExp, setCurrentExp, triggerBubble } = usePetStateStore();

  const { data } = useFetchPetInfo(matchId!);

  // 서버 exp → 현재 세팅
  useEffect(() => {
    if (data) setCurrentExp(data.exp);
  }, [data, setCurrentExp]);

  // exp 변화 감지 및 버블 처리
  useEffect(() => {
    if (currentExp === 0) return;

    const prevExp = parseInt(localStorage.getItem('prevExp') ?? '0', 10);

    if (currentExp > prevExp) {
      triggerBubble();
    }

    localStorage.setItem('prevExp', String(currentExp));
  }, [currentExp, triggerBubble]);

  return (
    <div className="w-full h-full flex flex-col items-center justify-center">
      <div className="fixed inset-0 pointer-events-none z-0">
        <AnimatePresence mode="wait">
          <MotionDiv
            key="deco-light"
            className="hidden md:block absolute inset-0 bg-deco-light"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.8 }}
          />
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

      <div className="relative z-10 flex flex-col items-center justify-center w-[252px]">
        <div className="fixed top-0 right-0 flex py-5 sm:hidden">
          <BellBtn />
        </div>
        <ExpBubble />
        <Bubbley className="mb-6" />
        <ExpBar />
      </div>
      <ChartModal />
    </div>
  );
}
