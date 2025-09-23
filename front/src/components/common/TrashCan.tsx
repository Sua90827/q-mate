'use client';
import React, { ReactEventHandler, useState } from 'react';
import { Button } from './Button';
import { Trash2 } from 'lucide-react';
import { useThemeStore } from '@/store/useThemeStore';

export default function TrashCan({ onClick }: { onClick: ReactEventHandler }) {
  const [active, setActive] = useState(false);
  const theme = useThemeStore((state) => state.theme);

  let activeClass = '';

  if (active) {
    if (theme === 'sunset') activeClass = 'bg-sunset-active text-secondary';
    else if (theme === 'night') activeClass = 'bg-night-active text-secondary';
    else activeClass = 'bg-primary text-secondary';
  }

  return (
    <Button
      className={`bg-transparent text-black shadow-none ${
        active ? activeClass : ''
      } mr-4 hover:rounded-md hover:opacity-80`}
      onClick={(e) => {
        setActive((prev) => !prev);
        onClick?.(e);
      }}
      theme={theme}
    >
      <Trash2 className="!w-[24px] !h-[24px]" />
    </Button>
  );
}
