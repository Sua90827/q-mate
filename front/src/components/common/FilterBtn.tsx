'use client';
import { Button } from '@/components/common/Button';
import { ListFilter } from 'lucide-react';
import React, { useState } from 'react';
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip';
import { TooltipPortal } from '@radix-ui/react-tooltip';

export default function FilterBtn({
  setShowCustomOnly,
  className,
}: {
  setShowCustomOnly: React.Dispatch<React.SetStateAction<boolean>>;
  className?: string | undefined;
}) {
  const [active, setActive] = useState(false);

  return (
    <TooltipProvider delayDuration={100}>
      <Tooltip>
        <TooltipTrigger asChild>
          <Button
            variant={'icon'}
            className={`p-3 shadow-none filter-btn cursor-pointer !pointer-events-auto ${
              active ? 'active p-3 rounded-md' : ''
            }${className}`}
            onClick={() => {
              setActive((prev) => !prev);
              setShowCustomOnly((prev) => !prev);
            }}
          >
            <ListFilter className="!w-[20px] !h-[20px]" />
          </Button>
        </TooltipTrigger>

        <TooltipPortal>
          <TooltipContent side="top" sideOffset={8} className="z-[9999]">
            <p>수정 가능한 커스텀 질문 필터링</p>
          </TooltipContent>
        </TooltipPortal>
      </Tooltip>
    </TooltipProvider>
  );
}
