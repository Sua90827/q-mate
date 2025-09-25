'use client';
import React, { ReactEventHandler, useState } from 'react';
import { Button } from './Button';
import { Trash2 } from 'lucide-react';

export default function TrashCan({ onClick }: { onClick: ReactEventHandler }) {
  const [active, setActive] = useState(false);

  return (
    <Button
      variant={'icon'}
      className={`text-theme-primary trash-btn ${active ? 'active' : ''} `}
      onClick={(e) => {
        setActive((prev) => !prev);
        onClick?.(e);
      }}
    >
      <Trash2 className="!w-6 !h-6" />
    </Button>
  );
}
