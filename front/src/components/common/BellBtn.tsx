'use client';
import { useThemeStore } from '@/store/useThemeStore';
import { Bell } from 'lucide-react';
import React, { useState } from 'react';

export default function BellBtn() {
  const [active, setActive] = useState(false);
  const theme = useThemeStore((state) => state.theme);

  let activeClass = '';

  if (active) {
    if (theme === 'sunset') activeClass = 'bg-sunset-active text-white';
    else if (theme === 'night') activeClass = 'bg-night-active text-white';
    else activeClass = 'bg-primary text-secondary';
  }

  return (
    <Bell
      className={`mr-7 !w-10 !h-10 relative  hover:opacity-80 hover:rounded-md p-2 ${
        active ? activeClass : ''
      }`}
      onClick={() => setActive((prev) => !prev)}
    />
  );
}
