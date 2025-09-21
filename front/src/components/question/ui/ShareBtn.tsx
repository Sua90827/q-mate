'use client';
import { Button } from '@/components/common/Button';
import { useThemeStore } from '@/store/useThemeStore';
import { Share2 } from 'lucide-react';
import React from 'react';

export default function ShareBtn() {
  const theme = useThemeStore((state) => state.theme);

  return (
    <Button
      className="w-16 h-16 rounded-full flex items-center justify-center absolute bottom-4 right-4"
      theme={theme}
    >
      <Share2 className="text-secondary" />
    </Button>
  );
}
