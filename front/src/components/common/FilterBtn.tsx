'use client';
import { Button } from '@/components/common/Button';
import { ListFilter } from 'lucide-react';
import React, { useState } from 'react';

export default function FilterBtn({
  setShowCustomOnly,
  className,
}: {
  setShowCustomOnly: React.Dispatch<React.SetStateAction<boolean>>;
  className?: string | undefined;
}) {
  const [active, setActive] = useState(false);

  return (
    <Button
      variant={'icon'}
      className={`p-3 shadow-none filter-btn ${active ? 'active p-3 rounded-md' : ''}${className}`}
      onClick={() => {
        setActive((prev) => !prev);
        setShowCustomOnly((prev) => !prev);
      }}
    >
      <ListFilter className="!w-[20px] !h-[20px]" />
    </Button>
  );
}
