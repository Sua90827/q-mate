'use client';
import { Button } from '@/components/common/Button';
import { useThemeStore } from '@/store/useThemeStore';
import { ListFilter } from 'lucide-react';
import React, { useState } from 'react';

export default function Filter({
  setShowCustomOnly,
}: {
  setShowCustomOnly: React.Dispatch<React.SetStateAction<boolean>>;
}) {
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
      } mr-4 hover:rounded-md hover:opacity-80   `}
      onClick={() => {
        setActive((prev) => !prev);
        setShowCustomOnly((prev) => !prev);
      }}
      theme={theme}
    >
      <ListFilter className="!w-[20px] !h-[20px] " />
    </Button>
  );
}
