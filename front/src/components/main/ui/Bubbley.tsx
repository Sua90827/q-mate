'use client';

import { usePetStateStore } from '@/store/usePetStore';
import { motion } from 'motion/react';
import Image from 'next/image';
import React, { useEffect, useState } from 'react';

type BubbleyProps = {
  className?: string;
};

export default function Bubbley({ className }: BubbleyProps) {
  const [theme, setTheme] = useState<string | null>(null);
  const [isJumping, setIsJumping] = useState(false);
  const exp = usePetStateStore((state) => state.currentExp); //현재 조회한 exp

  useEffect(() => {
    const current = document.documentElement.getAttribute('data-theme');
    setTheme(current);
  }, []);

  let src = '/images/bubbley/bubbley_baby.png';

  if (exp < 300) {
    src =
      theme === 'night'
        ? '/images/bubbley/bubbley_baby_night.png'
        : '/images/bubbley/bubbley_baby.png';
  } else if (exp < 500) {
    src =
      theme === 'night'
        ? '/images/bubbley/bubbley_child_night.png'
        : '/images/bubbley/bubbley_child.png';
  } else {
    src =
      theme === 'night'
        ? '/images/bubbley/bubbley_adult_night.png'
        : '/images/bubbley/bubbley_adult.png';
  }
  return (
    <div className={className}>
      <motion.div
        animate={isJumping ? { y: [0, -20, 0, -10, 0] } : { y: 0 }}
        transition={{
          type: 'tween',
          ease: 'easeInOut',
          duration: 0.7,
          times: [0, 0.3, 0.6, 0.8, 1],
        }}
        onAnimationComplete={() => setIsJumping(false)}
      >
        <Image
          src={src}
          alt="버블리 캐릭터"
          width={120}
          height={167}
          className="opacity-90"
          onClick={() => setIsJumping(true)}
        />
      </motion.div>
    </div>
  );
}
