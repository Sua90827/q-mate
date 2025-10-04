'use client';
import React from 'react';
import Image from 'next/image';
import { AnimatePresence, motion } from 'motion/react';
import { usePetStateStore } from '@/store/usePetStore';

export default function ExpBubble() {
  const bubbleTrigger = usePetStateStore((state) => state.bubbleTrigger);
  const resetBubble = usePetStateStore((state) => state.resetBubble);

  return (
    <AnimatePresence>
      {bubbleTrigger && (
        <motion.div
          initial={{ opacity: 0, y: -180, x: 0 }}
          animate={{
            opacity: [0, 1, 1, 0],
            y: [-180, -200, -220, -240, -260],
            x: [0, 15, -15, 10, -10, 0],
          }}
          exit={{ opacity: 0 }}
          transition={{
            duration: 1.6,
            ease: 'easeInOut',
            times: [0, 0.2, 0.4, 0.6, 0.8, 1],
          }}
          className="absolute w-[132px] h-[132px]"
          onAnimationComplete={() => resetBubble()}
        >
          <Image
            src="/images/bubble.png"
            alt="경험치 버블"
            fill
            className="rounded-full object-contain"
            sizes="100%"
          />
          <div className="absolute inset-0 flex flex-col items-center justify-center text-center gap-1 text-theme-primary">
            <span className="font-Gumi text-24 font-regular">EXP</span>
            <span className="font-Gumi text-14 font-regular">+10</span>
          </div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}
