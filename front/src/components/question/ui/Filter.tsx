'use client';
import { Button } from '@/components/common/Button';
import { ListFilter } from 'lucide-react';
import React, { useState } from 'react';

export default function Filter({
  setShowCustomOnly,
}: {
  setShowCustomOnly: React.Dispatch<React.SetStateAction<boolean>>;
}) {
  const [active, setActive] = useState(false);

  return (
    <Button
      className={`bg-transparent text-black shadow-none filter-btn ${
        active ? 'active' : ''
      } mr-4 hover:rounded-md hover:opacity-80`}
      onClick={() => {
        setActive((prev) => !prev);
        setShowCustomOnly((prev) => !prev);
      }}
    >
      <ListFilter className="!w-[20px] !h-[20px]" />
    </Button>
  );
}
