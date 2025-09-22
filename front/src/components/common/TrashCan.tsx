'use client';
import React, { ReactEventHandler, useState } from 'react';
import { Button } from './Button';
import { Trash2 } from 'lucide-react';

export default function TrashCan({ onClick }: { onClick: ReactEventHandler }) {
  const [active, setActive] = useState(false);

  return (
    <Button
      className={`bg-transparent text-black shadow-none trash-btn ${
        active ? 'active' : ''
      } mr-4 hover:rounded-md hover:opacity-80`}
      onClick={(e) => {
        setActive((prev) => !prev);
        onClick?.(e);
      }}
    >
      <Trash2 className="!w-[24px] !h-[24px]" />
    </Button>
  );
}
